package vn.com.loyalty.core.configuration.kafka;

import org.springframework.kafka.support.serializer.JsonSerializer;
import vn.com.loyalty.core.configuration.ApplicationConfig;

public class CustomJsonSerializer<T> extends JsonSerializer<T> {
    public CustomJsonSerializer() {
        super(ApplicationConfig.objectMapper());
    }
}
