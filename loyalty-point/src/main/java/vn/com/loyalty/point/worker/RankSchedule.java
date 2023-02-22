package vn.com.loyalty.point.worker;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.service.internal.RedisOperation;

@Component
@RequiredArgsConstructor
@Slf4j
public class RankSchedule {

    private final DayPointRepository dayPointRepository;
    private final MasterDataRepository masterDataRepository;
    private final RedisOperation redisOperation;
    private final EpointSpendRepository epointSpendRepository;
    private final EpointGainRepository epointGainRepository;
    private final TransactionRepository transactionRepository;
    private final RpointGainRepository rpointGainRepository;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private final EntityManager entityManager;

    @Scheduled(cron = "0/30 * * * * *")
    @SchedulerLock(name = Constants.SchedulerTaskName.EPOINT_TASK, lockAtLeastForString = "PT5M", lockAtMostForString = "PT14M")
    @Transactional
    public void rankSchedule() {




    }

    private void rankTask() {

    }

}
