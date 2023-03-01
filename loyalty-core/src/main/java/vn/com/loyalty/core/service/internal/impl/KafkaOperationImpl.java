package vn.com.loyalty.core.service.internal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.service.internal.KafkaOperation;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaOperationImpl implements KafkaOperation {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, Object>> send(String topic, Object data) {
        return kafkaTemplate.send(topic, data);
    }
}
