package vn.com.loyalty.core.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import vn.com.loyalty.core.dto.kafka.Reply;

import java.util.Map;

@Configuration
public class KafkaReplyingConfig {


//    @Bean("replyingKafkaTemplate")
//    public ReplyingKafkaTemplate<String, Object, Reply> replyingKafkaTemplate(ProducerFactory<String, Object> producerFactory
//            , KafkaMessageListenerContainer<String, Reply> listenerContainer) {
//        return new ReplyingKafkaTemplate<>(producerFactory, listenerContainer);
//    }
}
