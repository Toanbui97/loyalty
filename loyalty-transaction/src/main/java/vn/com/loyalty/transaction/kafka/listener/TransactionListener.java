package vn.com.loyalty.transaction.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.TransactionMessageDto;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.transaction.EPointEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionIncomeEntity;
import vn.com.loyalty.core.orchestration.Orchestration;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.TransactionIncomeService;
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
    private final TransactionIncomeService transactionIncomeService;
    private final TransactionService transactionService;
    private final RedisOperation redisOperation;
    private final EPointService ePointService;
    private final CmsWebClient webClient;

    @KafkaHandler
    @Transactional(rollbackFor = Exception.class)
    public String handleTransactionInCome(String message) throws JsonProcessingException {
        log.info("Kafka consumer receive message: {}", message);
        transactionIncomeService.saveTransactionIncome(TransactionIncomeEntity.builder().messageReceived(message).build());
        TransactionMessageDto transactionMessageDto = objectMapper.readValue(message, TransactionMessageDto.class);
        TransactionEntity transaction = TransactionEntity.builder()
                .customerCode(transactionMessageDto.getCustomerCode())
                .transactionTime(transactionMessageDto.getTransactionTime())
                .transactionId(transactionMessageDto.getTransactionId())
                .transactionValue(transactionMessageDto.getData().getTransactionValue())
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
        service.submit(() -> {
            this.updateGainPointCustomer(ePointEntity);
        });



        // TODO: handle rollback when call update customer failed
        service.shutdown();

        return "success";
    }

    private void updateTotalEpointRedis(EPointEntity ePointEntity){
        try {
            String key = redisOperation.genEpointKey(ePointEntity.getCustomerCode());
            if (redisOperation.hasValue(key)) {
                redisOperation.setValue(key, ePointEntity.getEpointGained().add(redisOperation.getValue(key)));
            } else {
                redisOperation.setValue(key, ePointEntity.getEpointGained());
            }
        } catch (Exception e) {
            redisOperation.discard();
        }
    }

    private BodyResponse<CustomerResponse> updateGainPointCustomer(EPointEntity ePointEntity) {
        return webClient.performUpdateCustomerInfo(BodyRequest.of(CustomerRequest.builder()
                .customerCode(ePointEntity.getCustomerCode())
                .totalEpoint(ePointEntity.getEpointGained())
                .gainedEloy(ePointEntity.getEpointGained())
                .build()));
    }


    private void handler(String message) {

        Orchestration orchestration = new Orchestration() {};
    }

}
