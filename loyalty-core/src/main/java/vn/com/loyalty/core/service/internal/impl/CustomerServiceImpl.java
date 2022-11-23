package vn.com.loyalty.core.service.internal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.utils.ObjectUtil;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.CustomerEntity;
import vn.com.loyalty.core.mapper.CustomerMapper;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.CustomerService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerResponse> getAllCustomer() {
        List<CustomerEntity> customerEntityList = customerRepository.findAll();
        return customerEntityList.stream().map(customerMapper::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        CustomerEntity customerEntity = customerMapper.DTOToEntity(customerRequest);
        customerEntity.setCustomerCode(this.generateCustomerCode());
        customerEntity = customerRepository.save(customerEntity);
        return customerMapper.entityToDTO(customerEntity);
    }

    @Override
    public CustomerResponse getCustomer(String customerCode)  {
        CustomerEntity customerEntity = customerRepository.findByCustomerCode(customerCode)
                .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, customerCode));
        return customerMapper.entityToDTO(customerEntity);
    }

    @Override
    public CustomerResponse updateCustomer(CustomerRequest customerRequest) {
        CustomerEntity customerEntity = customerRepository.findByCustomerCode(customerRequest.getCustomerCode())
                .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, customerRequest.getCustomerCode()));
        customerEntity = ObjectUtil.mergeObject(customerRequest, customerEntity);
        customerRepository.save(customerEntity);
        return customerMapper.entityToDTO(customerEntity);
    }

    private String generateCustomerCode() {
        return UUID.randomUUID().toString();
    }
}