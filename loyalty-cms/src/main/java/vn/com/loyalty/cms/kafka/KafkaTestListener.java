package vn.com.loyalty.cms.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(id = "class-level", topics = "test")
public class KafkaTestListener {

}
