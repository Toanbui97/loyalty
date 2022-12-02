package vn.com.loyalty.transaction.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.kafka.TransactionMessageDto;
import vn.com.loyalty.core.utils.ObjectUtil;

@Component
@KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
@Slf4j
public class TransactionListener {

    @KafkaHandler
    public void handleTransactionIncome(TransactionMessageDto message) {
      log.info(ObjectUtil.prettyPrintJsonObject(message));
    }

    @KafkaHandler
    public void handleTransactionInCome(String message) {
        log.info("Receive message: {}", message);
    }
}
