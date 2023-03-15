package vn.com.loyalty.core.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageLogging {

    private final ObjectMapper objectMapper;

    @Pointcut(value = "execution(* vn.com.loyalty.core.service.internal.KafkaOperation.send(String, Object)) && args(topic, data)", argNames = "topic,data")
    public void kafkaSendPointCut(String topic, Object data) {}

    @Pointcut("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public void kafkaReceivePointCut(){};

    @Before(value = "kafkaSendPointCut(topic, data)", argNames = "topic,data")
    public void kafkaSendLogging(String topic, Object data) throws JsonProcessingException {

        log.info("""
                 
                 ========================> Kafka Send
                 Topic: {}
                 Data: {}
                 """
                , topic
                , objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));

    }

    @Before(value = "kafkaReceivePointCut()")
    public void kafkaReceiveLogging(JoinPoint joinPoint) throws JsonProcessingException {
        String payload = (String) joinPoint.getArgs()[0];
        MessageHeaders headers = (MessageHeaders) joinPoint.getArgs()[1];

        log. info("""
                
                ========================> Kafka Receive
                Topic: {} - PartitionId: {} - Offset: {} - Consumer: {}
                Data: {}
                """
                , headers.get(KafkaHeaders.RECEIVED_TOPIC), headers.get(KafkaHeaders.RECEIVED_PARTITION)
                , headers.get(KafkaHeaders.OFFSET), headers.get(KafkaHeaders.CONSUMER)
                , objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readValue(payload, Object.class)));
    }
}
