package vn.com.loyalty.point.worker;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.dto.message.CustomerMessageDTO;
import vn.com.loyalty.core.entity.transaction.*;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.repository.specification.DayPointSpecs;
import vn.com.loyalty.core.repository.specification.TransactionSpecs;
import vn.com.loyalty.core.service.internal.RedisOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    private final RpointGainRepository rpointGainRepository;
    private final KafkaTemplate kafkaTemplate;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private final EntityManager entityManager;

    // at 0:00 AM
    @Scheduled(cron = "0/30 * * * * *")
    @SchedulerLock(name = Constants.SchedulerTaskName.EPOINT_TASK, lockAtLeastForString = "PT5M", lockAtMostForString = "PT14M")
    @Transactional
    public void scanEpointExpired() {
        LocalDateTime yesterday = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1L);

        List<DayPointEntity> dayPointExpiredList = dayPointRepository.findByEpointExpireTime(yesterday)
                .stream().map(e -> {
                    e.setStatus(PointStatus.DEACTIVE);
                    return e;
                })
                .collect(Collectors.toList());

        dayPointRepository.saveAll(dayPointExpiredList);
    }

    // after update epoint expired
    public void calculateDayEPoint(String customerCode) {

        LocalDateTime yesterday = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(1L);

        BigDecimal epointGain = epointGainRepository.findByCustomerCodeAndDay(customerCode, yesterday)
                .stream().map(EpointGainEntity::getEpoint)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        DayPointEntity dayPointEntity = DayPointEntity.builder().build();
        dayPointEntity.setEpointGain(epointGain);
        dayPointEntity.setEpointExpireTime(yesterday.minusMonths(Long.parseLong(masterDataRepository.findByKey(Constants.MasterDataKey.EPOINT_EXPIRE_TIME).getValue())));
        dayPointEntity.setEpointRemain(epointGain);
        dayPointEntity.setStatus(PointStatus.ACTIVE);
        dayPointEntity.setTransactionDay(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS));
        dayPointRepository.save(dayPointEntity);
    }

    private List<DayPointEntity> calculateEpoint(String customerCode) {

        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        LocalDateTime yesterday = today.minusDays(1L);

        List<DayPointEntity> dayPointEntityList = dayPointRepository.findAll(DayPointSpecs.byCustomerCodeAndActive(customerCode), DayPointSpecs.orderByDayDESC());

        BigDecimal epointSpend = epointSpendRepository.findByCustomerCodeAndDay(customerCode, yesterday).stream()
                .map(EpointSpendEntity::getEpoint)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (DayPointEntity dayPoint : dayPointEntityList) {
            if (epointSpend.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal usablePoint = dayPoint.getEpointGain().subtract(dayPoint.getEpointSpend());
                if (usablePoint.compareTo(epointSpend) > 0) {
                    dayPoint.setEpointSpend(dayPoint.getEpointSpend().add(epointSpend));
                    dayPoint.setEpointRemain(dayPoint.getEpointGain().subtract(dayPoint.getEpointSpend()));
                    epointSpend = BigDecimal.ZERO;
                } else {
                    dayPoint.setEpointSpend(dayPoint.getEpointSpend().add(usablePoint));
                    dayPoint.setEpointRemain(dayPoint.getEpointGain().subtract(dayPoint.getEpointSpend()));
                    epointSpend = epointSpend.subtract(usablePoint);
                    dayPoint.setStatus(PointStatus.DEACTIVE);
                }
            }
        }


        BigDecimal totalEPoint = dayPointEntityList.stream().map(DayPointEntity::getEpointRemain)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        redisOperation.setValue(redisOperation.genEpointKey( customerCode), totalEPoint.toString());

        kafkaTemplate.send(Constants.KafkaConstants.POINT_TOPIC, CustomerMessageDTO.builder()
                .customerCode(customerCode));


        return dayPointRepository.saveAll(dayPointEntityList);
    }


}
