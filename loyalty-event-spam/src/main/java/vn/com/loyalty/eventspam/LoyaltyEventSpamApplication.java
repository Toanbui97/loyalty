package vn.com.loyalty.eventspam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.KafkaOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootApplication
@EnableKafka
@EnableScheduling
@ComponentScan("vn.com.loyalty")
@EnableJpaRepositories("vn.com.loyalty.core.repository")
@EntityScan("vn.com.loyalty.core.entity")
@Slf4j
public class LoyaltyEventSpamApplication {

    @Autowired
    KafkaOperation kafkaOperation;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Scheduled(cron = "0/10 * * * * *")
    public void sendMessage() {

        TransactionMessageReq message = this.buildTransactionStockMessage();
        kafkaOperation.send(Constants.KafkaConstants.TRANSACTION_TOPIC, message)
                .whenComplete((result, throwable) -> {
                    try {
                        log.info("Kafka producer send message success to toppic - partition: {} - {} \n{}",
                                result.getProducerRecord().topic(),
                                result.getRecordMetadata().partition(),
                                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result.getProducerRecord().value()));
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }).exceptionally(e -> {
                    log.error("Kafka producer send message failed: {}", e.getMessage());
                    return null;
                });

    }


    public static void main(String[] args) {
        SpringApplication.run(LoyaltyEventSpamApplication.class, args);
    }

    private TransactionMessageReq buildTransactionStockMessage() {
        List<CustomerEntity> customerCode = customerRepository.findAll();

        return TransactionMessageReq.builder()
                .customerCode(customerCode.get(new Random().nextInt(customerCode.size())).getCustomerCode())
                .transactionId(UUID.randomUUID().toString())
                .transactionTime(LocalDateTime.now())
                .transactionType(TransactionType.STOCK_TYPE.getType())
                .data(TransactionMessageReq.Data.builder()
                        .transactionValue(BigDecimal.valueOf(new Random().nextInt(100000)))
                        .pointUse(BigDecimal.ZERO)
                        .build())
                .build();
    }

    private TransactionMessageReq buildTransactionBoundMessage() {


        return TransactionMessageReq.builder()
                .customerCode("9ab5c1b2-e4ac-4870-87cb-fd93682f21f" +  new Random().nextInt(10))
                .transactionId(UUID.randomUUID().toString())
                .transactionTime(LocalDateTime.now())
                .transactionType(TransactionType.BOUND_TYPE.getType())
                .data(TransactionMessageReq.Data.builder().transactionValue(BigDecimal.valueOf(new Random().nextInt())).build())
                .build();
    }

}
