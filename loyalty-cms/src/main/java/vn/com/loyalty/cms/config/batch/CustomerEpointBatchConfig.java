package vn.com.loyalty.cms.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.entity.cms.CustomerEntity_;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.CustomerService;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CustomerEpointBatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;


    public ItemReader<CustomerEntity> customerRepositoryItemReader() {
        RepositoryItemReader<CustomerEntity> reader = new RepositoryItemReader<>();
        reader.setRepository(customerRepository);
        reader.setPageSize(100);
        reader.setMethodName("findAll");
        reader.setSort(Map.of(CustomerEntity_.ID, Sort.Direction.ASC));
        return reader;
    }

    public ItemProcessor<CustomerEntity, CustomerEntity> epointProcessor() {
        return customerService::calculateEpoint;
    }

    public ItemWriter<CustomerEntity> customerRepositoryItemWriter() {
        RepositoryItemWriter<CustomerEntity> writer = new RepositoryItemWriter<>();
        writer.setMethodName("save");
        writer.setRepository(customerRepository);
        return writer;
    }

    @Bean(name = "customerEpointStep")
    public Step customerEpointStep(JobRepository jobRepository) {
        return new StepBuilder("customerEpointStep", jobRepository)
                .<CustomerEntity, CustomerEntity>chunk(100, transactionManager)
                .reader(customerRepositoryItemReader())
                .processor(epointProcessor())
                .writer(customerRepositoryItemWriter())
                .build();
    }

    @Bean(name = "customerEpointJob")
    public Job customerEpointJob(JobRepository jobRepository,
                                 @Qualifier(value = "customerEpointStep") Step step) {
            return new JobBuilder("customerEpointJob", jobRepository)
                    .start(step)
                    .build();
    }


}
