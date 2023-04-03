package vn.com.loyalty.transaction.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.transaction.config.EndPointProperties;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionMessage;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.entity.transaction.*;
import vn.com.loyalty.core.exception.CustomerPointException;
import vn.com.loyalty.core.exception.TransactionException;
import vn.com.loyalty.core.orchestration.Orchestration;
import vn.com.loyalty.core.service.internal.*;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.orchestration.OrchestrationStep;
import vn.com.loyalty.transaction.dto.VoucherMessage;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final TransactionMessageService transactionMessageService;
    private final TransactionService transactionService;
    private final RedisOperation redisOperation;
    private final WebClientService webClientService;
    private final EndPointProperties endPointProperties;

    @Transactional(rollbackFor = {Exception.class, TransactionException.class})
    @KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
    public void transactionListener(@Payload String payload, @Headers MessageHeaders headers) throws JsonProcessingException {
        transactionMessageService.saveMessage(TransactionMessageEntity.builder().messageReceived(payload).build());

        TransactionMessage message = objectMapper.readValue(payload, TransactionMessage.class);

        BigDecimal epointGain = transactionService.calculateEpointGain(message);
        BigDecimal rpointGain = transactionService.calculateRpointGain(message);
        BigDecimal epointSpend = message.getData().getPointUsed() != null ? message.getData().getPointUsed() : BigDecimal.ZERO;

        TransactionEntity transactionEntity = TransactionEntity.builder()
                .customerCode(message.getCustomerCode())
                .transactionTime(message.getTransactionTime())
                .transactionId(message.getTransactionId())
                .transactionValue(message.getData().getTransactionValue())
                .transactionType(TransactionType.valueOf(message.getTransactionType()))
                .epointGain(epointGain)
                .rpointGain(rpointGain)
                .epointSpend(epointSpend)
                .voucherDetailCodeList(message.getData().getVoucherDetailCodeList())
                .build();

        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> transactionService.saveTransaction(transactionEntity)),
                CompletableFuture.runAsync(() -> this.savePointToRedis(transactionEntity)),
                CompletableFuture.runAsync(() -> this.processOrchestration(transactionEntity))
        ).exceptionally(throwable -> {
            throw new TransactionException(throwable.getMessage());
        }).join();

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
            BigDecimal rpoint = redisOperation.getValue(rpointKey, BigDecimal.class);
            redisOperation.setValue(rpointKey, rpoint.add(transaction.getRpointGain()));
        } else {
            redisOperation.setValue(rpointKey, transaction.getRpointGain());
        }

    }


    private void processOrchestration(TransactionEntity transaction) {
        TransactionOrchestrationMessage transactionOrchestrationMessage = TransactionOrchestrationMessage.builder()
                .transactionId(transaction.getTransactionId())
                .customerCode(transaction.getCustomerCode())
                .rpointGain(transaction.getRpointGain())
                .epointGain(transaction.getEpointGain())
                .epointSpend(transaction.getEpointSpend())
                .voucherDetailCodeList(transaction.getVoucherDetailCodeList())
                .build();

        Orchestration.ofSteps(new OrchestrationStep(transactionOrchestrationMessage) {
            @Override
            public BodyResponse<OrchestrationMessage> sendProcess (BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getCmsService().getBaseUrl(),
                        endPointProperties.getCmsService().getProcessOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }

            @Override
            public BodyResponse<OrchestrationMessage> sendRollback (BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getCmsService().getBaseUrl(),
                        endPointProperties.getCmsService().getRollbackOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }
        },new OrchestrationStep(transactionOrchestrationMessage) {
            @Override
            public BodyResponse<OrchestrationMessage> sendProcess(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getVoucherService().getBaseUrl(),
                        endPointProperties.getVoucherService().getProcessOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }

            @Override
            public BodyResponse<OrchestrationMessage> sendRollback(BodyRequest<OrchestrationMessage> request) {
                return webClientService.postSync(endPointProperties.getVoucherService().getBaseUrl(),
                        endPointProperties.getVoucherService().getRollbackOrchestrationTransaction(),
                        request,
                        BodyResponse.class);
            }
        }).asyncProcessOrchestration();
    }

}
