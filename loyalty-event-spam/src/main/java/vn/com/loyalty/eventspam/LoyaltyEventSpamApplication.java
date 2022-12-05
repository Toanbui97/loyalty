package vn.com.loyalty.eventspam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.concurrent.Future;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.FutureResult;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.kafka.TransactionMessageDto;
import vn.com.loyalty.core.utils.ObjectUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(cron = "0/15 * * * * *")
    private void sendMessage() {
        TransactionMessageDto message = this.buildTransactionStockMessage();
        ListenableFuture<SendResult<String, Object>> sendResult = kafkaTemplate.send(Constants.KafkaConstants.TRANSACTION_TOPIC, message);
        sendResult.addCallback(result -> {
            log.info("Kafka producer send message success to toppic - partition: {} - {} \n{}",
                    result.getProducerRecord().topic(),
                    result.getRecordMetadata().partition(),
                    ObjectUtil.prettyPrintJsonObject(result.getProducerRecord().value()));
        }, ex -> {
            log.error("Kafka producer send message failed: {} ", ex.getMessage());
        });
    }


    public static void main(String[] args) {
        SpringApplication.run(LoyaltyEventSpamApplication.class, args);
    }

    private TransactionMessageDto buildTransactionStockMessage() {


        return TransactionMessageDto.builder()
                .customerCode("9ab5c1b2-e4ac-4870-87cb-fd93682f21fa")
                .transactionId(UUID.randomUUID().toString())
                .transactionTime(LocalDateTime.now().toString())
                .transactionType(TransactionType.STOCK_TYPE.getType())
                .data(TransactionMessageDto.Data.builder().transactionValue(BigDecimal.valueOf(92187321).toString()).build())
                .build();
    }

    private TransactionMessageDto buildTransactionBoundMessage() {
        return TransactionMessageDto.builder()
                .customerCode("9ab5c1b2-e4ac-4870-87cb-fd93682f21fa")
                .transactionId(UUID.randomUUID().toString())
                .transactionTime(LocalDateTime.now().toString())
                .transactionType(TransactionType.BOUND_TYPE.getType())
                .data(TransactionMessageDto.Data.builder().transactionValue(BigDecimal.valueOf(921857321).toString()).build())
                .build();
    }

}
