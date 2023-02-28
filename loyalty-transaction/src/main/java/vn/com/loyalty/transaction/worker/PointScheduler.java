package vn.com.loyalty.transaction.worker;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;
import vn.com.loyalty.core.entity.cms.EpointGainEntity_;
import vn.com.loyalty.core.entity.cms.EpointSpendEntity;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.repository.specification.EpointGainSpecs;
import vn.com.loyalty.core.service.internal.RedisOperation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointScheduler {

    private final MasterDataRepository masterDataRepository;
    private final RedisOperation redisOperation;
    private final EpointSpendRepository epointSpendRepository;
    private final EpointGainRepository epointGainRepository;
    private final TransactionRepository transactionRepository;
    private final RpointRepository rpointRepository;
    private final KafkaTemplate kafkaTemplate;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private final EntityManager entityManager;

    // at 0:00 AM
    @Scheduled(cron = "0/30 * * * * *")
    @SchedulerLock(name = Constants.SchedulerTaskName.DEACTIVATE_EPOINT, lockAtLeastForString = "PT5M", lockAtMostForString = "PT14M")
    @Transactional
    public void deactivateEpointExpire() {
        LocalDate yesterday = LocalDate.now().minusDays(1L);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<EpointGainEntity> update = criteriaBuilder.createCriteriaUpdate(EpointGainEntity.class);
        Root<EpointGainEntity> root = update.from(EpointGainEntity.class);

        update.where(criteriaBuilder.equal(root.get(EpointGainEntity_.EXPIRE_DAY), yesterday))
                .set(root.get(EpointGainEntity_.STATUS), PointStatus.DEACTIVE);

        entityManager.createQuery(update).executeUpdate();
    }


    private void calculateEpoint(String customerCode) {

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1L);

        List<EpointGainEntity> epointList = epointGainRepository.findAll(
                EpointGainSpecs.byCustomerCodeAndStatus(customerCode, PointStatus.ACTIVE),
                EpointGainSpecs.orderByExpireDayDESC());

        BigDecimal epointSpend = epointSpendRepository.findByCustomerCodeAndTransactionDay(customerCode, yesterday).stream()
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

        redisOperation.setValue(redisOperation.genEpointKey( customerCode), totalEPoint.toString());

        epointGainRepository.saveAll(epointList);
    }


}
