package vn.com.loyalty.cms.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class SpringBatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setTransactionManager(transactionManager);
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTablePrefix("CMS.BATCH_");
        jobRepositoryFactoryBean.afterPropertiesSet();
        return jobRepositoryFactoryBean.getObject();
    }

}
