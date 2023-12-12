package vn.com.loyalty.core.service.internal.impl.cms;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.cms.*;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.repository.EpointGainRepository;
import vn.com.loyalty.core.repository.EpointSpendRepository;
import vn.com.loyalty.core.repository.RpointRepository;
import vn.com.loyalty.core.repository.specification.EpointGainSpecs;
import vn.com.loyalty.core.repository.specification.RpointGainSpecs;
import vn.com.loyalty.core.service.internal.MasterDataService;
import vn.com.loyalty.core.service.internal.RankHistoryService;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.impl.RankService;
import vn.com.loyalty.core.utils.ObjectUtil;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
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
    private final RpointRepository rpointRepository;
    private final RankHistoryService rankHistoryService;
    private final RankService rankService;
    private final MasterDataService masterDataService;


    @Override
    @Transactional
    public CustomerResponse signIn(String customerName) {

        CustomerEntity customerEntity = customerRepository.findFirstByCustomerName(customerName)
                .orElseGet(() -> customerRepository.save(CustomerEntity.builder().customerName(customerName).build()));

        redisOperation.setValue(redisOperation.genEpointKey(customerEntity.getCustomerCode()), customerEntity.getEpoint());
        redisOperation.setValue(redisOperation.genRpointKey(customerEntity.getCustomerCode()), customerEntity.getRpoint());

        return customerMapper.entityToDTO(customerEntity);
    }
    @Override
    public Page<CustomerResponse> getListCustomer(Pageable pageable) {
        Page<CustomerEntity> customerEntityPage = customerRepository.findAll(pageable);
        return customerEntityPage.map(customerMapper::entityToDTO);
    }

    @Override
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        CustomerEntity customerEntity = customerMapper.DTOToEntity(customerRequest);
        customerEntity.setCustomerCode(this.generateCustomerCode());
        customerEntity = customerRepository.save(customerEntity);
        redisOperation.setValue(redisOperation.genEpointKey(customerEntity.getCustomerCode()), customerEntity.getEpoint());
        redisOperation.setValue(redisOperation.genRpointKey(customerEntity.getCustomerCode()), customerEntity.getRpoint());
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
    @Transactional
    public CustomerResponse deleteCustomer(String customerCode) {
        CustomerEntity customerEntity = customerRepository.findByCustomerCode(customerCode).orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, customerCode));
        redisOperation.delete(redisOperation.genEpointKey(customerCode));
        redisOperation.delete(redisOperation.genRpointKey(customerCode));
        customerRepository.delete(customerEntity);
        return customerMapper.entityToDTO(customerEntity);
    }

    @Override
    public CustomerEntity calculateEPoint(CustomerEntity customerEntity) {
        List<EpointGainEntity> gainList = epointGainRepository.findAll(
                EpointGainSpecs.byCustomerCodeAndStatus(customerEntity.getCustomerCode(), PointStatus.ACTIVE),
                EpointGainSpecs.orderByExpireDayDESC());

        List<EpointSpendEntity> spendList = epointSpendRepository.findByCustomerCodeAndStatus(customerEntity.getCustomerCode(), PointStatus.UNCOUNTED);
        BigDecimal spendNumber = spendList.stream()
                .map(EpointSpendEntity::getEpoint)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (EpointGainEntity point : gainList) {
            if (spendNumber.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usablePoint = point.getEpointRemain();
                if (usablePoint.compareTo(spendNumber) > 0) {
                    point.setEpointRemain(usablePoint.subtract(spendNumber));
                    spendNumber = BigDecimal.ZERO;
                } else {
                    point.setEpointRemain(BigDecimal.ZERO);
                    spendNumber = spendNumber.subtract(usablePoint);
                    point.setStatus(PointStatus.DEACTIVATE);
                }
            }
        }

        BigDecimal gainNumber = gainList.stream().map(EpointGainEntity::getEpointRemain)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        epointSpendRepository.saveAllAndFlush(spendList.stream().map(spend -> {
            spend.setStatus(PointStatus.COUNTED);
            return spend;
        }).toList());

        redisOperation.setValue(redisOperation.genEpointKey( customerEntity.getCustomerCode()), gainNumber);
        customerEntity.setEpoint(gainNumber);
        epointGainRepository.saveAllAndFlush(gainList);
        return customerEntity;
    }

    @Override
    public CustomerEntity calculateRank(CustomerEntity customerEntity) {

        RankHistoryEntity lastUpdatedRank = rankHistoryService.getLastUpdated(customerEntity.getCustomerCode());
        List<RpointEntity> rpointGainFromLastUpdate = rpointRepository.findAll(RpointGainSpecs.fromLastUpdate(lastUpdatedRank.getUpdatedDate()));
        BigDecimal totalFromLastUpdate = rpointGainFromLastUpdate.stream().map(RpointEntity::getRpoint).reduce(BigDecimal.ZERO, BigDecimal::add);
        RankEntity currentRank = rankService.getRankByCode(customerEntity.getRankCode());

        // case down rank
        if (totalFromLastUpdate.compareTo(currentRank.getKeepPoint()) < 0) {
            RankEntity inferiorityRank = rankService.getInferiorityRank(currentRank);
            customerEntity.setRankCode(inferiorityRank.getRankCode());
            customerEntity.setRpoint(inferiorityRank.getRequirePoint());
        } else {
        // case keep rank
             customerEntity.setRankCode(currentRank.getRankCode());
             customerEntity.setRpoint(currentRank.getRequirePoint());
        }

        customerEntity.setRankExpired(LocalDate.now().plusMonths(masterDataService.getValue(Constants.MasterDataKey.RANK_EXPIRE_TIME, Long.class)));
        customerEntity.setLastUpdatedRank(LocalDate.now());

        // update new point to redis
        redisOperation.setValue(redisOperation.genRpointKey(customerEntity.getCustomerCode()), customerEntity.getRpoint());
        customerRepository.save(customerEntity);
        this.saveHistoryRankUpdate(customerEntity, totalFromLastUpdate);

        return customerEntity;
    }

    @Override
    public void saveHistoryRankUp(CustomerEntity customerEntity) {
        RankHistoryEntity lastUpdatedRank = rankHistoryService.getLastUpdated(customerEntity.getCustomerCode());
        List<RpointEntity> listFromLastUpdate = rpointRepository.findAll(RpointGainSpecs.fromLastUpdate(lastUpdatedRank.getUpdatedDate()));
        BigDecimal totalFromLastUpdate = listFromLastUpdate.stream().map(RpointEntity::getRpoint).reduce(BigDecimal.ZERO, BigDecimal::add);
        this.saveHistoryRankUpdate(customerEntity, totalFromLastUpdate);
    }

    private void saveHistoryRankUpdate(CustomerEntity customerEntity, BigDecimal totalFromLastUpdate) {
        rankHistoryService.saveHistory(RankHistoryEntity.builder()
                .customerCode(customerEntity.getCustomerCode())
                .updatedDate(LocalDate.now())
                .rpointGain(totalFromLastUpdate)
                .rankCode(customerEntity.getRankCode())
                .build());
    }

    private String generateCustomerCode() {
        return UUID.randomUUID().toString();
    }
}
