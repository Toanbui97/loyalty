package vn.com.vpbanks.loyalty.loyalty.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.vpbanks.loyalty.core.dto.request.BaseRequest;
import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.vpbanks.loyalty.core.service.internal.CustomerService;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.vpbanks.loyalty.core.utils.factory.response.ResponseFactory;


@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final ResponseFactory responseFactory;

    @GetMapping()
    public ResponseEntity<BodyResponse<CustomerResponse>> receiveCustomerInfoList() {
        return responseFactory.success(customerService.getAllCustomer());
    }

    @PostMapping
    public ResponseEntity<BodyResponse<CustomerResponse>> createCustomerInfo(@RequestBody BaseRequest<CustomerRequest> req) {

        return responseFactory.success(customerService.createCustomer(req.getData()));
    }

}
