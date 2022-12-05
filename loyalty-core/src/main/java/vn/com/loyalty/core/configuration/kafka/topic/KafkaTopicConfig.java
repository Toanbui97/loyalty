package vn.com.loyalty.core.configuration.kafka.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import vn.com.loyalty.core.constant.Constants;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic transactionTopic() {
        return TopicBuilder.name(Constants.KafkaConstants.TRANSACTION_TOPIC).build();
    }

    @Bean
    public NewTopic customerTopic() {
        return TopicBuilder.name(Constants.KafkaConstants.CUSTOMER_TOPIC).build();
    }
}
