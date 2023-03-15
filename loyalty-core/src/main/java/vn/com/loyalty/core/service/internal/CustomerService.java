package vn.com.loyalty.core.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;

public interface CustomerService {

    Page<CustomerResponse> getListCustomer(Pageable pageable);

    CustomerResponse createCustomer(CustomerRequest customerRequest);

    CustomerResponse getCustomer(String customerCode) throws ResourceNotFoundException;

    CustomerResponse updateCustomer(CustomerRequest customerRequest);

    CustomerEntity calculateEPoint(CustomerEntity customerEntity);

}
