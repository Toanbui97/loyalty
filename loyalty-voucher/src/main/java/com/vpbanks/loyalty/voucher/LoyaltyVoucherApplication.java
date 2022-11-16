package com.vpbanks.loyalty.voucher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan("vn.com.vpbanks.loyalty")
@EnableJpaRepositories("vn.com.vpbanks.loyalty.core.repository")
@EntityScan("vn.com.vpbanks.loyalty.core.entity")
public class LoyaltyVoucherApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyaltyVoucherApplication.class, args);
    }

}
