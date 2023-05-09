package vn.com.loyalty.eventspam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.service.internal.KafkaOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RequestMapping("/")
@RestController
@Slf4j
public class TestController {

    @Autowired
    KafkaOperation kafkaOperation;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    ObjectMapper objectMapper;


    @GetMapping("/")
    public void a(@RequestBody BodyRequest<?> req) {
        this.sendMessage();
    }

    private void sendMessage() {
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

    private TransactionMessageReq buildTransactionStockMessage() {
        List<CustomerEntity> customerCode = customerRepository.findAll();

        return TransactionMessageReq.builder()
                .customerCode("83c881a7-1d38-40b7-b52d-8f4247f8acc6")
                .transactionId(UUID.randomUUID().toString())
                .transactionTime(LocalDateTime.now())
                .transactionType(TransactionType.STOCK_TYPE.getType())
                .data(TransactionMessageReq.Data.builder()
                        .transactionValue(BigDecimal.valueOf(new Random().nextInt(10000)))
//                        .pointToDiscount(BigDecimal.valueOf(new Random().nextInt(5)))
                        .build())
                .build();
    }
}
