package vn.com.loyalty.transaction.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.entity.transaction.*;
import vn.com.loyalty.core.service.internal.*;
import vn.com.loyalty.transaction.service.OrchestrationService;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final TransactionMessageService transactionMessageService;
    private final OrchestrationService orchestrationService;

//    @Transactional(rollbackFor = {Exception.class, TransactionException.class})
//    @KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
    public void transactionListener(@Payload String payload, @Headers MessageHeaders headers) throws JsonProcessingException {
        transactionMessageService.saveMessage(TransactionMessageEntity.builder().messageReceived(payload).build());

        TransactionMessageReq message = objectMapper.readValue(payload, TransactionMessageReq.class);
        orchestrationService.processTransactionOrchestration(message);
    }
}
