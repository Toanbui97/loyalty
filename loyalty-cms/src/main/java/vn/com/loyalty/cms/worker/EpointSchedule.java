package vn.com.loyalty.cms.worker;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;
import vn.com.loyalty.core.entity.cms.EpointGainEntity_;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EpointSchedule {

    private final JobLauncher jobLauncher;
    private final Job calculateEpointJob;

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

    @Scheduled(cron = "1 * * * * *")
    @SchedulerLock(name = Constants.SchedulerTaskName.SCHEDULE_EPOINT, lockAtLeastForString = "PT10M", lockAtMostForString = "PT1H")
    @Transactional
    public void launchEpointJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(LocalDateTime.now()))
                .toJobParameters();

        jobLauncher.run(calculateEpointJob, jobParameters);
    }
}
