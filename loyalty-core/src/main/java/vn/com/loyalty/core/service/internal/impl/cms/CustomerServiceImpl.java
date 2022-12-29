package vn.com.loyalty.core.service.internal.impl.cms;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.dto.message.CustomerMessageDto;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.utils.ObjectUtil;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.mapper.CustomerMapper;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.CustomerService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Page<CustomerResponse> getListCustomer(Pageable pageable) {
        Page<CustomerEntity> customerEntityPage = customerRepository.findAll(pageable);
        return customerEntityPage.map(customerMapper::entityToDTO);
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

    @Override
    public void handlePointGained(CustomerMessageDto customerMessageDto) {
        if (customerRepository.existsByCustomerCode(customerMessageDto.getCustomerCode())) {
            CustomerEntity customerEntity = customerRepository.findByCustomerCode(customerMessageDto.getCustomerCode())
                    .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, customerMessageDto.getCustomerCode()));
            customerEntity.setTotalEloy(customerEntity.getTotalEloy().add(customerMessageDto.getEloyGained()));
            customerEntity.setTotalEpoint(customerEntity.getTotalEpoint().add(customerMessageDto.getEpointGained()));
            customerRepository.save(customerEntity);
        } else {
            customerRepository.save(CustomerEntity.builder()
                    .customerCode(customerMessageDto.getCustomerCode())
                    .totalEloy(customerMessageDto.getEloyGained())
                    .totalEpoint(customerMessageDto.getEpointGained())
                    .build());
        }
    }

    private String generateCustomerCode() {
        return UUID.randomUUID().toString();
    }
}
