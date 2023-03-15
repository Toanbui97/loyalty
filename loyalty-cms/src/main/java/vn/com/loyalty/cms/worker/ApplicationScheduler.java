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
import org.springframework.beans.factory.annotation.Qualifier;
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
public class ApplicationScheduler {

    private final JobLauncher jobLauncher;
    @Qualifier(value = "customerEPointJob")
    private final Job calculateEPointJob;

    @PersistenceContext(type = PersistenceContextType.TRANSACTION)
    private final EntityManager entityManager;

    // at 0:00 AM
//    @Scheduled(cron = "0/30 * * * * *")
//    @SchedulerLock(name = Constants.SchedulerTaskName.DEACTIVATE_EPOINT, lockAtLeastForString = "PT5M", lockAtMostForString = "PT14M")
    @Transactional
    public void deactivatePointExpire() {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<EpointGainEntity> update = criteriaBuilder.createCriteriaUpdate(EpointGainEntity.class);
        Root<EpointGainEntity> root = update.from(EpointGainEntity.class);

        update.where(criteriaBuilder.equal(root.get(EpointGainEntity_.EXPIRE_DAY), LocalDate.now().minusDays(1)))
                .set(root.get(EpointGainEntity_.STATUS), PointStatus.DEACTIVATE);

        entityManager.createQuery(update).executeUpdate();
    }

//    @Scheduled(cron = "0/15 * * * * *")
    @SchedulerLock(name = Constants.SchedulerTaskName.SCHEDULE_EPOINT, lockAtLeastForString = "PT10M", lockAtMostForString = "PT1H")
    public void launchEPointJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(LocalDateTime.now()))
                .toJobParameters();

        jobLauncher.run(calculateEPointJob, jobParameters);
    }
}
