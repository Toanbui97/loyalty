package vn.com.loyalty.core.configuration.kafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

import static vn.com.loyalty.core.constant.Constants.ServiceConfig.KAFKA_PROPERTIES_PREFIX;

//
//@Setter
//@Getter
//@Configuration
//@RequiredArgsConstructor
//@ConfigurationProperties(prefix = KAFKA_PROPERTIES_PREFIX)
//public class KafkaProducerConfig {
//
//    private Integer retries;
//    private String bootstrapServers;
//    private Integer requestTimeout;
//    private Integer deliveryTimeout;
//
//    private final KafkaProperties.Producer producer = new KafkaProperties.Producer();
//
//    @Primary
//    @Bean
//    public ProducerFactory<String, Object> kafkaProducerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put("retries", retries);
//        props.put("bootstrap.servers", bootstrapServers);
//        props.put("key.serializer", StringSerializer.class);
//        props.put("value.serializer", JsonSerializer.class);
//        props.put("request.timeout.ms", requestTimeout);
//        props.put("delivery.timeout.ms", deliveryTimeout);
//
//        return new DefaultKafkaProducerFactory<>(props);
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate() {
//        return new KafkaTemplate<>(kafkaProducerFactory());
//    }
//
//}
