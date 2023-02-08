package vn.com.loyalty.core.service.internal.impl.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.cache.CustomerPointCache;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.service.internal.RedisOperation;
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
    private final RedisOperation redisOperation;
    private final ObjectMapper objectMapper;

    @Override
    public Page<CustomerResponse> getListCustomer(Pageable pageable) {
        Page<CustomerEntity> customerEntityPage = customerRepository.findAll(pageable);

        return customerEntityPage.map(customer -> {

            CustomerPointCache customerPointCache = redisOperation.getValue(redisOperation.genEpointKey(customer.getCustomerCode()), CustomerPointCache.class);
            customer.setTotalRpoint(customerPointCache.getRpoint());
            customer.setTotalEpoint(customerPointCache.getEpoint());

            customerRepository.save(customer);

            return customerMapper.entityToDTO(customer);
        });
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
        CustomerPointCache customerPointCache = redisOperation.getValue(redisOperation.genEpointKey(customerEntity.getCustomerCode()), CustomerPointCache.class);

        if (!customerPointCache.getEpoint().equals(customerEntity.getTotalEpoint())
                || !customerPointCache.getRpoint().equals(customerEntity.getTotalEpoint())) {

            customerEntity.setTotalEpoint(customerEntity.getTotalEpoint());
            customerEntity.setTotalRpoint(customerPointCache.getRpoint());
            customerRepository.save(customerEntity);
        }

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
    public CustomerResponse updateGainPoint(CustomerRequest customerRequest) {

        CustomerEntity customerEntity = customerRepository.findByCustomerCode(customerRequest.getCustomerCode())
                .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, customerRequest.getCustomerCode()));

        return null;
    }

    private String generateCustomerCode() {
        return UUID.randomUUID().toString();
    }
}
