package vn.com.loyalty.cms.worker.spring.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import vn.com.loyalty.core.entity.BaseEntity_;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.CustomerService;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CustomerEPointBatch {

    private final PlatformTransactionManager transactionManager;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;


    private ItemReader<CustomerEntity> customerRepositoryItemReader() {
        RepositoryItemReader<CustomerEntity> reader = new RepositoryItemReader<>();
        reader.setRepository(customerRepository);
        reader.setPageSize(5);
        reader.setMethodName("findAll");
        reader.setSort(Map.of(BaseEntity_.ID, Sort.Direction.ASC));
        return reader;
    }

    private ItemProcessor<CustomerEntity, CustomerEntity> ePointProcessor() {
        return customerService::calculateEPoint;
    }

    private ItemWriter<CustomerEntity> customerRepositoryItemWriter() {
        RepositoryItemWriter<CustomerEntity> writer = new RepositoryItemWriter<>();
        writer.setMethodName("save");
        writer.setRepository(customerRepository);
        return writer;
    }

    private Step customerEPointStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("customerEPointStep", jobRepository)
                .<CustomerEntity, CustomerEntity>chunk(5, transactionManager)
                .reader(this.customerRepositoryItemReader())
                .processor(this.ePointProcessor())
                .writer(this.customerRepositoryItemWriter())
                .build();
    }

    @Bean(name = "customerEPointJob")
    public Job customerEPointJob(JobRepository jobRepository) {
            return new JobBuilder("customerEPointJob", jobRepository)
                    .start(this.customerEPointStep(jobRepository, transactionManager))
                    .build();
    }


}
