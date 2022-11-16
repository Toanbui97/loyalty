package vn.com.vpbanks.loyalty.core.service.internal;

import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomer();

    CustomerResponse createCustomer(CustomerRequest customerRequest);
}
