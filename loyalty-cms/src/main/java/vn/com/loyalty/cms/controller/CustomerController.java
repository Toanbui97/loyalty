package vn.com.loyalty.cms.controller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import vn.com.loyalty.cms.service.OrchestrationService;
import vn.com.loyalty.cms.worker.ApplicationScheduler;
import vn.com.loyalty.core.annotation.HasRole;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.CustomerService;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;


@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ResponseFactory responseFactory;
    private final CustomerRepository customerRepository;
    private final ApplicationScheduler applicationScheduler;

    @PostMapping("/receiveCustomer/{customerCode}")
    public ResponseEntity<BodyResponse<CustomerResponse>> receiveCustomerInfo(@RequestBody BodyRequest<CustomerRequest> req,
                                                                              @PathVariable String customerCode) {
        return responseFactory.success(customerService.getCustomer(customerCode));
    }

    @PostMapping("/performCreateCustomer")
    public ResponseEntity<BodyResponse<CustomerResponse>> createCustomerInfo(@RequestBody BodyRequest<CustomerRequest> req) {
        return responseFactory.success(customerService.createCustomer(req.getData()));
    }

    @PostMapping("/performUpdateCustomer")
    public ResponseEntity<BodyResponse<CustomerResponse>> updateCustomerInfo(@RequestBody BodyRequest<CustomerRequest> req) {
        return responseFactory.success(customerService.updateCustomer(req.getData()));
    }

    @PostMapping("/receiveCustomerList")
    public ResponseEntity<BodyResponse<CustomerResponse>> receiveCustomerInfoList(@RequestBody BodyRequest<?> req, @PageableDefault Pageable pageable) {
        return responseFactory.success(customerService.getListCustomer(pageable));
    }


    @PostMapping(value = {"/executeCustomerEpointJob/{customerCode}", "/executeCustomerEpointJob"})
    public ResponseEntity<BodyResponse<CustomerResponse>> performCustomerEpointJob(@RequestBody BodyRequest<CustomerRequest> req, @PathVariable @Nullable String customerCode) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {

        if (StringUtils.hasText(customerCode)) {
            customerService.calculateEPoint(customerRepository.findByCustomerCode(customerCode)
                    .orElse(new CustomerEntity()));
            return responseFactory.success(customerService.getCustomer(customerCode));
        } else {
            applicationScheduler.launchEPointJob();
            return responseFactory.success(customerService.getListCustomer(Pageable.unpaged()));
        }
    }

    @PostMapping("/executeDeactivatePointJob")
    public ResponseEntity<BodyResponse<Object>> performExecuteDeactivatePointJob(@RequestBody BodyRequest<?> req) {
        applicationScheduler.deactivatePointExpire();
        return responseFactory.success();
    }

    @PostMapping(value = {"/executeCustomerRpointJob/{customerCode}", "/executeCustomerRpointJob"})
    public ResponseEntity<BodyResponse<CustomerResponse>> performCustomerRPointJob(@RequestBody BodyRequest<CustomerRequest> req, @PathVariable @Nullable String customerCode) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        if (StringUtils.hasText(customerCode)) {
            customerService.calculateRank(customerRepository.findByCustomerCode(customerCode)
                    .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, customerCode)));
            return responseFactory.success(customerService.getCustomer(customerCode));
        } else {
            applicationScheduler.launchRPointJob();
            return responseFactory.success(customerService.getListCustomer(Pageable.unpaged()));
        }
    }

}
