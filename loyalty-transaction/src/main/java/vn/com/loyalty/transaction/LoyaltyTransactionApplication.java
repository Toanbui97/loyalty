package vn.com.loyalty.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@ComponentScan("vn.com.loyalty")
@EnableJpaRepositories("vn.com.loyalty.core.repository")
@EntityScan("vn.com.loyalty.core.entity")
@PropertySource("classpath:application.yaml")
@EnableCaching
public class LoyaltyTransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyaltyTransactionApplication.class, args);
    }

}
