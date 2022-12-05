package vn.com.loyalty.transaction.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.internal.Futures;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.kafka.CustomerMessageDto;
import vn.com.loyalty.core.dto.kafka.TransactionMessageDto;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.transaction.EPointEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionIncomeEntity;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.TransactionIncomeService;
import vn.com.loyalty.core.service.internal.TransactionService;
import vn.com.loyalty.core.service.internal.impl.EPointService;
import vn.com.loyalty.core.thirdparty.service.CmsWebClient;
import vn.com.loyalty.core.utils.ObjectUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final TransactionIncomeService transactionIncomeService;
    private final TransactionService transactionService;
    private final RedisOperation redisOperation;
    private final CmsWebClient cmsWebClient;
    private final EPointService ePointService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaHandler
    public String handleTransactionInCome(String message) throws JsonProcessingException {
        log.info("Kafka consumer receive message: {}", message);
        transactionIncomeService.saveTransactionIncome(TransactionIncomeEntity.builder().messageReceived(message).build());
        TransactionMessageDto transactionMessageDto = objectMapper.readValue(message, TransactionMessageDto.class);
        TransactionEntity transaction = TransactionEntity.builder()
                .customerCode(transactionMessageDto.getCustomerCode())
                .transactionTime(LocalDateTime.parse(transactionMessageDto.getTransactionTime()))
                .transactionId(transactionMessageDto.getTransactionId())
                .transactionValue(BigDecimal.valueOf(Long.valueOf(transactionMessageDto.getData().getTransactionValue())))
                .transactionType(TransactionType.valueOf(transactionMessageDto.getTransactionType()))
                .build();
        transaction = transactionService.saveTransaction(transaction);

        BigDecimal gainPoint = transactionService.calculateGainPoint(transaction);
        EPointEntity ePointEntity = EPointEntity.builder()
                .customerCode(transaction.getCustomerCode())
                .epointGained(gainPoint)
                .transactionId(transaction.getTransactionId())
                .transactionValue(transaction.getTransactionValue())
                .expireTime(LocalDateTime.now().minusMonths(6))
                .build();

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(() -> {
            this.updateTotalEpointRedis(ePointEntity);
        });
        service.execute(() -> {
            ePointService.saveEpoint(ePointEntity);
        });
        service.execute(() -> {
            this.updateEpointCustomer(ePointEntity);
        });

        service.shutdown();

        return "success";
    }

    private void updateTotalEpointRedis(EPointEntity ePointEntity){
        String key = redisOperation.genEpointKey(ePointEntity.getCustomerCode());
        if (redisOperation.hasValue(key)) {
            redisOperation.setValue(key, ePointEntity.getEpointGained().add(redisOperation.getValue(key)));
        } else {
            redisOperation.setValue(key, ePointEntity.getEpointGained());
        }
    }

    private void updateEpointCustomer(EPointEntity ePointEntity) {

        CustomerMessageDto message = CustomerMessageDto.builder()
                .customerCode(ePointEntity.getCustomerCode())
                .epointGained(ePointEntity.getEpointGained())
                .eloyGained(ePointEntity.getEpointGained())
                .build();
        kafkaTemplate.send(Constants.KafkaConstants.CUSTOMER_TOPIC, message).addCallback(result -> {
            log.info("Kafka send message success: {} - {} \n {}", Constants.KafkaConstants.CUSTOMER_TOPIC, result.getProducerRecord().partition(),
                    ObjectUtil.prettyPrintJsonObject(message));
        }, ex -> {
            // TODO: implement outbox pattern
            log.error("Kafka send message failed: {} - \n{} \n {}", Constants.KafkaConstants.CUSTOMER_TOPIC,
                    ObjectUtil.prettyPrintJsonObject(message), ex.getMessage());
        });
    }
}
