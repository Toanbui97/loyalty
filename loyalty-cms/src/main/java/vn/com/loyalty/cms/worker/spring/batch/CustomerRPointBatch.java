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
import vn.com.loyalty.core.service.internal.impl.RankService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class CustomerRPointBatch {

    private final PlatformTransactionManager transactionManager;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService;
    private final RankService rankService;

    private ItemReader<CustomerEntity> customerRepositoryItemReader() {
        RepositoryItemReader<CustomerEntity> reader = new RepositoryItemReader<>();
        reader.setRepository(customerRepository);
        reader.setPageSize(5);
        reader.setMethodName("findByRankExpired");
        reader.setArguments(List.of(LocalDate.now()));
        return reader;
    }

    private ItemWriter<CustomerEntity> customerRepositoryItemWriter() {
        RepositoryItemWriter<CustomerEntity> writer = new RepositoryItemWriter<>();
        writer.setMethodName("save");
        writer.setRepository(customerRepository);
        return writer;
    }

    private ItemProcessor<CustomerEntity, CustomerEntity> customerItemProcessor() {
        return customerService::calculateRank;
    }

    private Step customerRPointStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("customerRpointStep", jobRepository)
                .<CustomerEntity, CustomerEntity>chunk(5, transactionManager)
                .reader(this.customerRepositoryItemReader())
                .processor(this.customerItemProcessor())
                .writer(this.customerRepositoryItemWriter())
                .build();
    }

    @Bean(name = "customerRPointJob")
    public Job customerEPointJob(JobRepository jobRepository) {
        return new JobBuilder("customerRPointJob", jobRepository)
                .start(this.customerRPointStep(jobRepository, transactionManager))
                .build();
    }
}
