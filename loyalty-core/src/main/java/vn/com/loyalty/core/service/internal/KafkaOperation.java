package vn.com.loyalty.core.service.internal;

import org.springframework.kafka.support.SendResult;
import org.springframework.lang.Nullable;

import java.util.concurrent.CompletableFuture;

public interface KafkaOperation {
    CompletableFuture<SendResult<String, Object>> send(String topic, @Nullable Object data);
}
