package vn.com.loyalty.point.worker;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.CustomerPointStatus;
import vn.com.loyalty.core.entity.transaction.CustomerPointEntity;
import vn.com.loyalty.core.entity.transaction.EpointGainEntity;
import vn.com.loyalty.core.entity.transaction.EpointSpendEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.repository.specification.CustomerPointSpecs;
import vn.com.loyalty.core.repository.specification.TransactionSpecs;
import vn.com.loyalty.core.service.internal.RedisOperation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PointScheduler {

    private final CustomerPointRepository customerPointRepository;
    private final MasterDataRepository masterDataRepository;
    private final RedisOperation redisOperation;
    private final EpointSpendRepository epointSpendRepository;
    private final EpointGainRepository epointGainRepository;
    private final TransactionRepository transactionRepository;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private final EntityManager entityManager;

    @Transactional
    public void deactivePointExpire() {
        List<CustomerPointEntity> customerPointEntityExpiredList = customerPointRepository.findByTransactionDay(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1L));
        customerPointRepository.saveAll(customerPointEntityExpiredList.stream().map(customerPointEntity -> {
            customerPointEntity.setStatus(CustomerPointStatus.DEACTIVE);
            return customerPointEntity;
        }).toList());
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void customerPointSchedule() {

        List<TransactionEntity> transactionEntityList = transactionRepository.findAll(TransactionSpecs.inYesterday());
        Set<String> customerCodeSet = transactionEntityList.stream().map(TransactionEntity::getCustomerCode).collect(Collectors.toSet());
        long batchSize = 20;
        for (String customerCode : customerCodeSet) {
            try {
                redisOperation.begin();
                if (customerCodeSet.size() % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
                this.epointSchedule(customerCode);
            } catch (Exception e) {
                redisOperation.rollback();
                e.printStackTrace();
            }
        }
    }


    public void epointSchedule(String customerCode) {
        LocalDateTime yesterday = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1L);

        BigDecimal epointGain = epointGainRepository.findByCustomerCodeAndDay(customerCode, yesterday)
                .stream().map(EpointGainEntity::getEpointGain)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal epointSpend = epointSpendRepository.findByCustomerCodeAndDay(customerCode, yesterday)
                .stream().map(EpointSpendEntity::getEpointSpend)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CustomerPointEntity customerPointEntity = CustomerPointEntity.builder().build();
        customerPointEntity.setEpointGain(epointGain);
        customerPointEntity.setExpireTime(LocalDateTime.now().minusMonths(Long.parseLong(masterDataRepository.findByKey(Constants.MasterDataKey.EPOINT_EXPIRE_TIME).getValue())).truncatedTo(ChronoUnit.DAYS));
        customerPointEntity.setRemainPoint(epointGain);

        customerPointRepository.save(customerPointEntity);


        List<CustomerPointEntity> customerPointActiveList = customerPointRepository.findAll(CustomerPointSpecs.byCustomerCodeAndActive(customerCode), CustomerPointSpecs.orderByDayDESC());
        customerPointActiveList = this.calculateEpoint(epointSpend, customerPointActiveList);

        BigDecimal totalPoint = customerPointActiveList.stream().map(CustomerPointEntity::getRemainPoint)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        redisOperation.setValue(redisOperation.genEpointKey( customerCode), totalPoint.toString());

    }

    private List<CustomerPointEntity> calculateEpoint(BigDecimal epointSpend, List<CustomerPointEntity> customerPointEntityList) {

        for (CustomerPointEntity customerPoint : customerPointEntityList) {
            if (epointSpend.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usablePoint = customerPoint.getEpointGain().subtract(customerPoint.getEpointSpend());
                if (usablePoint.compareTo(epointSpend) > 0) {
                    customerPoint.setEpointSpend(customerPoint.getEpointSpend().add(epointSpend));
                    customerPoint.setRemainPoint(customerPoint.getEpointGain().subtract(customerPoint.getEpointSpend()));
                    epointSpend = BigDecimal.ZERO;
                } else {
                    customerPoint.setEpointSpend(customerPoint.getEpointSpend().add(usablePoint));
                    customerPoint.setRemainPoint(customerPoint.getEpointGain().subtract(customerPoint.getEpointSpend()));
                    epointSpend = epointSpend.subtract(usablePoint);
                    customerPoint.setStatus(CustomerPointStatus.DEACTIVE);
                }
            }
        }

        return customerPointRepository.saveAll(customerPointEntityList);
    }

}
