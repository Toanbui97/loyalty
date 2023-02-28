package vn.com.loyalty.cms;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("vn.com.loyalty")
@EnableJpaRepositories("vn.com.loyalty.core.repository")
@EntityScan("vn.com.loyalty.core.entity")
@PropertySource("classpath:application.yaml")
@EnableScheduling
@EnableCaching
@EnableBatchProcessing(tablePrefix = "CMS.BATCH_")
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class LoyaltyCmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyaltyCmsApplication.class, args);
    }

}
