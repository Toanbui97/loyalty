package vn.com.loyalty.point.worker;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.CustomerPointStatus;
import vn.com.loyalty.core.entity.transaction.DayPointEntity;
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
@Slf4j
public class PointScheduler {

    private final DayPointRepository dayPointRepository;
    private final MasterDataRepository masterDataRepository;
    private final RedisOperation redisOperation;
    private final EpointSpendRepository epointSpendRepository;
    private final EpointGainRepository epointGainRepository;
    private final TransactionRepository transactionRepository;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private final EntityManager entityManager;

    @Scheduled(cron = "0/30 * * * * *")
    @SchedulerLock(name = Constants.SchedulerTaskName.EPOINT_TASK, lockAtLeastForString = "PT5M", lockAtMostForString = "PT14M")
    @Transactional
    public void customerPointSchedule() {
        log.info("=============================================>EPOINT SCHEDULE START");
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
                this.pointSchedule(customerCode);
            } catch (Exception e) {
                redisOperation.rollback();
                log.error("Customer code: {}", customerCode);
                e.printStackTrace();
            }
        }
    }


    public void pointSchedule(String customerCode) {

        LocalDateTime yesterday = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1L);

        BigDecimal epointGain = epointGainRepository.findByCustomerCodeAndDay(customerCode, yesterday)
                .stream().map(EpointGainEntity::getEpointGain)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal epointSpend = epointSpendRepository.findByCustomerCodeAndDay(customerCode, yesterday)
                .stream().map(EpointSpendEntity::getEpointSpend)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DayPointEntity dayPointEntity = DayPointEntity.builder().build();
        dayPointEntity.setEpointGain(epointGain);
        dayPointEntity.setExpireTime(LocalDateTime.now().minusMonths(Long.parseLong(masterDataRepository.findByKey(Constants.MasterDataKey.EPOINT_EXPIRE_TIME).getValue())).truncatedTo(ChronoUnit.DAYS));
        dayPointEntity.setRemainPoint(epointGain);
        dayPointEntity.setStatus(CustomerPointStatus.ACTIVE);
        dayPointEntity.setTransactionDay(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        dayPointRepository.save(dayPointEntity);


        List<DayPointEntity> customerPointActiveList = dayPointRepository.findAll(CustomerPointSpecs.byCustomerCodeAndActive(customerCode), CustomerPointSpecs.orderByDayDESC());
        customerPointActiveList = this.updateEpoint(epointSpend, customerPointActiveList);

        BigDecimal totalPoint = customerPointActiveList.stream().map(DayPointEntity::getRemainPoint)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        redisOperation.setValue(redisOperation.genEpointKey( customerCode), totalPoint.toString());
        log.info("Customer: {} - EpointGain: {} - EpointSpend: {}" ,  customerCode, epointGain, epointSpend);
    }

    private List<DayPointEntity> updateEpoint(BigDecimal epointSpend, List<DayPointEntity> dayPointEntityList) {

        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

        for (DayPointEntity customerPoint : dayPointEntityList) {

            // calculate epoint
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

            // update status if expire
            if (customerPoint.getExpireTime().isAfter(today)) customerPoint.setStatus(CustomerPointStatus.DEACTIVE);
        }

        return dayPointRepository.saveAll(dayPointEntityList);
    }

}
