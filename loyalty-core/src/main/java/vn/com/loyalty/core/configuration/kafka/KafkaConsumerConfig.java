package vn.com.loyalty.core.configuration.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class KafkaConsumerConfig {
//
//    private final KafkaProperties.Consumer consumer = new KafkaProperties.Consumer();
//
//    @Value("${spring.kafka.producer.bootstrap-servers}")
//    private String bootstrapServer;
//
//    @Bean
//    public ConsumerFactory<String, Object> customConsumerFactory() {
//
//        String groupId = consumer.getGroupId();
//        Map<String, Object> props = new HashMap<>();
//        props.put("bootstrap.servers", bootstrapServer);
//        if (Objects.nonNull(groupId)) {
//            props.put("group.id", groupId);
//        } else {
//            props.put("group.id", "default");
//        }
//        props.put("key.deserializer", StringDeserializer.class);
//        props.put("value.deserializer", JsonDeserializer.class);
//
//        return new DefaultKafkaConsumerFactory<>(props);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Object> customKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(customConsumerFactory());
//        return factory;
//    }
//}
