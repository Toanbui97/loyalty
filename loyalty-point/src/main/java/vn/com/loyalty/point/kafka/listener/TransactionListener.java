package vn.com.loyalty.point.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.TransactionMessageDTO;
import vn.com.loyalty.core.entity.transaction.GainPointEntity;
import vn.com.loyalty.core.entity.transaction.SpendPointEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;
import vn.com.loyalty.core.exception.TransactionException;
import vn.com.loyalty.core.repository.GainPointRepository;
import vn.com.loyalty.core.repository.MasterDataRepository;
import vn.com.loyalty.core.repository.SpendPointRepository;
import vn.com.loyalty.core.service.internal.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final TransactionMessageService transactionMessageService;
    private final TransactionService transactionService;
    private final RedisOperation redisOperation;
    private final MasterDataRepository masterDataRepository;
    private final GainPointRepository gainPointRepository;
    private final SpendPointRepository spendPointRepository;

    @KafkaHandler
    @Transactional(rollbackFor = {Exception.class, TransactionException.class})
    public void handleTransactionInCome(String message) {
        try {
            log.info("========================> Kafka Message \n{}", message);
            transactionMessageService.saveMessage(TransactionMessageEntity.builder().messageReceived(message).build());

            TransactionMessageDTO transactionMessage = objectMapper.readValue(message, TransactionMessageDTO.class);

            redisOperation.begin();

            BigDecimal epointGain = transactionService.calculateEpointGain(transactionMessage);
            BigDecimal rpointGain = transactionService.calculateRpointGain(transactionMessage);
            BigDecimal epointSpend = transactionMessage.getData().getPointToDiscount() != null
                    ? transactionMessage.getData().getPointToDiscount() : BigDecimal.ZERO;

            TransactionEntity transactionEntity = transactionService.saveTransaction(TransactionEntity.builder()
                    .customerCode(transactionMessage.getCustomerCode())
                    .transactionTime(transactionMessage.getTransactionTime())
                    .transactionId(transactionMessage.getTransactionId())
                    .transactionValue(transactionMessage.getData().getTransactionValue())
                    .transactionType(TransactionType.valueOf(transactionMessage.getTransactionType()))
                    .transactionDiscount(transactionMessage.getData().getTransactionValue())
                    .pointToDiscount(epointSpend)
                    .epointGain(epointGain)
                    .rpointGain(rpointGain)
                    .build());

            gainPointRepository.save(GainPointEntity.builder()
                    .customerCode(transactionMessage.getCustomerCode())
                    .epointGain(epointGain)
                    .rpointGain(rpointGain)
                    .transactionId(transactionMessage.getTransactionId())
                    .expireTime(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusMonths(Long.parseLong(masterDataRepository.findByKey(Constants.MasterDataKey.EPOINT_EXPIRE_TIME).getValue())))
                    .build());

            if (transactionMessage.getData().getPointToDiscount() != null) {
                spendPointRepository.save(SpendPointEntity.builder()
                        .transactionId(transactionMessage.getCustomerCode())
                        .epointSpend(transactionMessage.getData().getPointToDiscount())
                        .customerCode(transactionMessage.getCustomerCode()).build());
            }

            this.savePointToRedis(transactionEntity);
            redisOperation.commit();
        } catch (Exception e) {
            e.printStackTrace();
            redisOperation.rollback();
            throw new TransactionException(e.getMessage());
        }

    }

    private void savePointToRedis(TransactionEntity transaction) {

        String epointKey = redisOperation.genEpointKey(transaction.getCustomerCode());

        BigDecimal epoint = redisOperation.hasValue(epointKey) ? redisOperation.getValue(epointKey) : BigDecimal.ZERO;

        if (epoint.compareTo(transaction.getPointToDiscount()) < 0) {
            // TODO throw error. not enough point to use
        }

        redisOperation.setValue(epointKey, epoint.add(transaction.getEpointGain()).subtract(transaction.getPointToDiscount()));

        String rpointKey = redisOperation.genRpointKey(transaction.getCustomerCode());
        if (redisOperation.hasValue(rpointKey)) {
            BigDecimal rpoint = new BigDecimal(redisOperation.getValue(epointKey).toString());
            redisOperation.setValue(epointKey, rpoint.add(transaction.getRpointGain()));
        } else {
            redisOperation.setValue(rpointKey, transaction.getRpointGain());
        }

    }
}
