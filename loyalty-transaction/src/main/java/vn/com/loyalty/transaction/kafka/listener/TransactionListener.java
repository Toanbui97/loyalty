package vn.com.loyalty.transaction.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.CustomerMessage;
import vn.com.loyalty.core.dto.message.TransactionMessage;
import vn.com.loyalty.core.entity.transaction.*;
import vn.com.loyalty.core.exception.CustomerPointException;
import vn.com.loyalty.core.exception.TransactionException;
import vn.com.loyalty.core.service.internal.*;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final TransactionMessageService transactionMessageService;
    private final TransactionService transactionService;
    private final RedisOperation redisOperation;
    private final KafkaOperation kafkaOperation;

    @Transactional(rollbackFor = {Exception.class, TransactionException.class})
    @KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
    public void transactionListener(@Payload String payload, @Headers MessageHeaders headers) {
        try {
            transactionMessageService.saveMessage(TransactionMessageEntity.builder().messageReceived(payload).build());

            TransactionMessage message = objectMapper.readValue(payload, TransactionMessage.class);

            BigDecimal epointGain = transactionService.calculateEpointGain(message);
            BigDecimal rpointGain = transactionService.calculateRpointGain(message);
            BigDecimal epointSpend = message.getData().getPointToDiscount() != null
                    ? message.getData().getPointToDiscount() : BigDecimal.ZERO;

            TransactionEntity transactionEntity = TransactionEntity.builder()
                    .customerCode(message.getCustomerCode())
                    .transactionTime(message.getTransactionTime())
                    .transactionId(message.getTransactionId())
                    .transactionValue(message.getData().getTransactionValue())
                    .transactionType(TransactionType.valueOf(message.getTransactionType()))
                    .epointGain(epointGain)
                    .rpointGain(rpointGain)
                    .epointSpend(epointSpend)
                    .build();

            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> transactionService.saveTransaction(transactionEntity)),
                    CompletableFuture.runAsync(() -> this.savePointToRedis(transactionEntity)))
                    .exceptionally(throwable -> {
                        throw new TransactionException(throwable.getMessage());
                    }).join();

            kafkaOperation.send(Constants.KafkaConstants.POINT_TOPIC, CustomerMessage.builder()
                            .transactionId(message.getTransactionId())
                            .customerCode(message.getCustomerCode())
                            .data(CustomerMessage.Data.builder()
                                            .rpointGain(rpointGain)
                                            .epointGain(epointGain)
                                            .epointSpend(epointSpend)
                                            .build()
                                    )
                            .build());

        } catch (Exception ignored) {

        }

    }

    private void savePointToRedis(TransactionEntity transaction) {

        // save epoint to redis
        String epointKey = redisOperation.genEpointKey(transaction.getCustomerCode());
        BigDecimal epoint = redisOperation.hasValue(epointKey) ? redisOperation.getValue(epointKey, BigDecimal.class) : BigDecimal.ZERO;

        if (epoint.compareTo(transaction.getEpointSpend()) < 0) {
            throw new CustomerPointException(transaction.getTransactionId(), transaction.getCustomerCode(), epoint, transaction.getEpointSpend());
        }
        redisOperation.setValue(epointKey, epoint.add(transaction.getEpointGain()).subtract(transaction.getEpointSpend()));

        // save rpoint to redis
        String rpointKey = redisOperation.genRpointKey(transaction.getCustomerCode());
        if (redisOperation.hasValue(rpointKey)) {
            BigDecimal rpoint = redisOperation.getValue(epointKey, BigDecimal.class);
            redisOperation.setValue(epointKey, rpoint.add(transaction.getRpointGain()));
        } else {
            redisOperation.setValue(rpointKey, transaction.getRpointGain());
        }
    }

}
