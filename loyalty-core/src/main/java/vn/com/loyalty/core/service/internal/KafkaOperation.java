package vn.com.loyalty.core.service.internal;

import jakarta.annotation.Nullable;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

public interface KafkaOperation {
    CompletableFuture<SendResult<String, Object>> send(String topic, @Nullable Object data);
}
