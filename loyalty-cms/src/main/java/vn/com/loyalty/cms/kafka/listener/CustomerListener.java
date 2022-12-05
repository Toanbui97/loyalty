package vn.com.loyalty.cms.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.kafka.CustomerMessageDto;
import vn.com.loyalty.core.service.internal.CustomerService;

@KafkaListener(topics = Constants.KafkaConstants.CUSTOMER_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerListener {

    private final CustomerService customerService;
    private final ObjectMapper objectMapper;

    @KafkaHandler
    public void handleCustomerInCome(String message) throws JsonProcessingException {
        log.info("Kafka consumer receive message: {} \n{}", Constants.KafkaConstants.CUSTOMER_TOPIC, message);
        CustomerMessageDto customer = objectMapper.readValue(message, CustomerMessageDto.class);
        customerService.handlePointGained(customer);
    }
}
