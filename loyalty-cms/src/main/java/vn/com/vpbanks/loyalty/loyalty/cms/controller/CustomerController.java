package vn.com.vpbanks.loyalty.loyalty.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.vpbanks.loyalty.core.dto.request.BaseRequest;
import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.vpbanks.loyalty.core.exception.ResourceNotFoundException;
import vn.com.vpbanks.loyalty.core.service.internal.CustomerService;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.vpbanks.loyalty.core.utils.factory.response.ResponseFactory;


@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ResponseFactory responseFactory;

    @PostMapping("/receiveCustomer")
    public ResponseEntity<BodyResponse<CustomerResponse>> receiveCustomerInfo(@RequestBody BaseRequest<CustomerRequest> req) throws ResourceNotFoundException {
        return responseFactory.success(customerService.getCustomer(req.getData().getCustomerCode()));
    }

    @PostMapping("/performCreateCustomer")
    public ResponseEntity<BodyResponse<CustomerResponse>> createCustomerInfo(@RequestBody BaseRequest<CustomerRequest> req) {
        return responseFactory.success(customerService.createCustomer(req.getData()));
    }

    @PostMapping("/performUpdateCustomer")
    public ResponseEntity<BodyResponse<CustomerResponse>> updateCustomerInfo(@RequestBody BaseRequest<CustomerRequest> req) throws ResourceNotFoundException {
        return responseFactory.success(customerService.updateCustomer(req.getData()));
    }

    @GetMapping("/receiveCustomerList")
    public ResponseEntity<BodyResponse<CustomerResponse>> receiveCustomerInfoList() {
        return responseFactory.success(customerService.getAllCustomer());
    }


}
