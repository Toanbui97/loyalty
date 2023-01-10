package vn.com.loyalty.transaction.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.TransactionMessageDto;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.transaction.EPointEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.TransactionMessageService;
import vn.com.loyalty.core.service.internal.TransactionService;
import vn.com.loyalty.core.service.internal.impl.EPointService;
import vn.com.loyalty.core.thirdparty.service.CmsWebClient;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final TransactionMessageService transactionMessageService;
    private final TransactionService transactionService;
    private final RedisOperation redisOperation;
    private final EPointService ePointService;

    @KafkaHandler
    public void handleTransactionInCome(String message) throws JsonProcessingException {
        log.info("========================> Kafka Message \n{}", message);
        transactionMessageService.saveMessage(TransactionMessageEntity.builder().messageReceived(message).build());

        TransactionMessageDto transactionMessageDto = objectMapper.readValue(message, TransactionMessageDto.class);

        TransactionEntity transaction = TransactionEntity.builder()
                .customerCode(transactionMessageDto.getCustomerCode())
                .transactionTime(transactionMessageDto.getTransactionTime())
                .transactionId(transactionMessageDto.getTransactionId())
                .transactionValue(transactionMessageDto.getData().getTransactionValue())
                .transactionType(TransactionType.valueOf(transactionMessageDto.getTransactionType()))
                .build();

        transaction = transactionService.saveTransaction(transaction);

        EPointEntity ePointEntity = EPointEntity.builder()
                .customerCode(transaction.getCustomerCode())
                .epointGained(transactionService.calculateGainPoint(transaction))
                .transactionId(transaction.getTransactionId())
                .transactionValue(transaction.getTransactionValue())
                .expireTime(LocalDateTime.now().minusMonths(6))
                .build();

        ePointService.saveEpoint(ePointEntity);

        this.updateTotalEpointRedis(ePointEntity);

    }

    private void updateTotalEpointRedis(EPointEntity ePointEntity){
        try {
            String key = redisOperation.genEpointKey(ePointEntity.getCustomerCode());
            if (redisOperation.hasValue(key)) {
                redisOperation.setValue(key, ePointEntity.getEpointGained().add(BigDecimal.valueOf( (Double) redisOperation.getValue(key))));
            } else {
                redisOperation.setValue(key, ePointEntity.getEpointGained());
            }
        } catch (Exception e) {
            redisOperation.discard();
        }
    }

}
