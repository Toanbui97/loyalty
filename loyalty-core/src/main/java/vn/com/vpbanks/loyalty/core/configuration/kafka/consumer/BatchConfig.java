package vn.com.vpbanks.loyalty.core.configuration.kafka.consumer;

import vn.com.vpbanks.loyalty.core.dto.KafkaPoolPrefixDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableKafka
@Configuration
public class BatchConfig {

    public BatchConfig(ConcurrentKafkaListenerContainerFactory<String, Object> customKafkaListenerContainerFactory, KafkaPoolPrefixDto kafkaPoolPrefixDto) {

        ConsumerFactory<? super String, Object> consumerFactory = customKafkaListenerContainerFactory.getConsumerFactory();
        Map<String, Object> props = new HashMap<>(consumerFactory.getConfigurationProperties());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaPoolPrefixDto.getMaxPollRecords());
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaPoolPrefixDto.getMaxPollInterval());

        DefaultKafkaConsumerFactory<String, Object> consumerFactoryBatch = new DefaultKafkaConsumerFactory<>(props);
        customKafkaListenerContainerFactory.setConsumerFactory(consumerFactoryBatch);
        customKafkaListenerContainerFactory.setBatchListener(true);
        log.info("------------------ Config Batch to consumer batch message -----------------");
        log.info("Properties: " + customKafkaListenerContainerFactory.getConsumerFactory().getConfigurationProperties());
        log.info("----------------------------------------------------------------------------------");
    }
}
