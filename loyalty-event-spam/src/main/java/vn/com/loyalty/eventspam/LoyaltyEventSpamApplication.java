package vn.com.loyalty.eventspam;

import io.github.classgraph.ClassGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.TransactionMessageDTO;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.utils.ObjectUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@SpringBootApplication
@EnableKafka
@EnableScheduling
@ComponentScan("vn.com.loyalty")
@EnableJpaRepositories("vn.com.loyalty.core.repository")
@EntityScan("vn.com.loyalty.core.entity")
@Slf4j
public class LoyaltyEventSpamApplication {

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    CustomerRepository customerRepository;


    @Scheduled(cron = "0/15 * * * * *")
    public void sendMessage() {

        TransactionMessageDTO message = this.buildTransactionStockMessage();
        kafkaTemplate.send(Constants.KafkaConstants.TRANSACTION_TOPIC, message)
                .whenComplete((result, throwable) -> {
                    log.info("Kafka producer send message success to toppic - partition: {} - {} \n{}",
                            result.getProducerRecord().topic(),
                            result.getRecordMetadata().partition(),
                            ObjectUtil.prettyPrintJsonObject(result.getProducerRecord().value()));
                }).exceptionally(e -> {
                    log.error("Kafka producer send message failed: {}", e.getMessage());
                    return null;
                });

    }


    public static void main(String[] args) {
        SpringApplication.run(LoyaltyEventSpamApplication.class, args);
    }

    private TransactionMessageDTO buildTransactionStockMessage() {
        List<CustomerEntity> customerCode = customerRepository.findAll();

        return TransactionMessageDTO.builder()
                .customerCode(customerCode.get(new Random().nextInt(customerCode.size())).getCustomerCode())
                .transactionId(UUID.randomUUID().toString())
                .transactionTime(LocalDateTime.now())
                .transactionType(TransactionType.STOCK_TYPE.getType())
                .data(TransactionMessageDTO.Data.builder().transactionValue(BigDecimal.valueOf(new Random().nextInt(10000))).build())
                .build();
    }

    private TransactionMessageDTO buildTransactionBoundMessage() {


        return TransactionMessageDTO.builder()
                .customerCode("9ab5c1b2-e4ac-4870-87cb-fd93682f21f" +  new Random().nextInt(10))
                .transactionId(UUID.randomUUID().toString())
                .transactionTime(LocalDateTime.now())
                .transactionType(TransactionType.BOUND_TYPE.getType())
                .data(TransactionMessageDTO.Data.builder().transactionValue(BigDecimal.valueOf(new Random().nextInt())).build())
                .build();
    }

}
