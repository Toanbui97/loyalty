package vn.com.loyalty.core.service.internal.impl.cms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;
import vn.com.loyalty.core.entity.cms.EpointSpendEntity;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.repository.EpointGainRepository;
import vn.com.loyalty.core.repository.EpointSpendRepository;
import vn.com.loyalty.core.repository.specification.EpointGainSpecs;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.utils.ObjectUtil;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.mapper.CustomerMapper;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.CustomerService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final RedisOperation redisOperation;
    private final EpointGainRepository epointGainRepository;
    private final EpointSpendRepository epointSpendRepository;

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
    public CustomerEntity calculateEpoint(CustomerEntity customerEntity) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1L);

        List<EpointGainEntity> epointList = epointGainRepository.findAll(
                EpointGainSpecs.byCustomerCodeAndStatus(customerEntity.getCustomerCode(), PointStatus.ACTIVE),
                EpointGainSpecs.orderByExpireDayDESC());

        BigDecimal epointSpend = epointSpendRepository.findByCustomerCodeAndTransactionDay(customerEntity.getCustomerCode(), yesterday).stream()
                .map(EpointSpendEntity::getEpoint)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (EpointGainEntity point : epointList) {
            if (epointSpend.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usablePoint = point.getEpointRemain();
                if (usablePoint.compareTo(epointSpend) > 0) {
                    point.setEpointRemain(usablePoint.subtract(epointSpend));
                    epointSpend = BigDecimal.ZERO;
                } else {
                    point.setEpointRemain(BigDecimal.ZERO);
                    epointSpend = epointSpend.subtract(usablePoint);
                    point.setStatus(PointStatus.DEACTIVE);
                }
            }
        }

        BigDecimal totalEPoint = epointList.stream().map(EpointGainEntity::getEpointRemain)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        redisOperation.setValue(redisOperation.genEpointKey( customerEntity.getCustomerCode()), totalEPoint);
        customerEntity.setEpoint(totalEPoint);
        epointGainRepository.saveAll(epointList);
        return customerEntity;
    }

    private String generateCustomerCode() {
        return UUID.randomUUID().toString();
    }
}
