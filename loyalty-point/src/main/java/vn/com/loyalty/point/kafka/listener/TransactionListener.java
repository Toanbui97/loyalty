package vn.com.loyalty.point.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.TransactionType;
import vn.com.loyalty.core.dto.message.TransactionMessageDTO;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.entity.cms.RankEntity;
import vn.com.loyalty.core.entity.transaction.*;
import vn.com.loyalty.core.exception.CustomerPointException;
import vn.com.loyalty.core.exception.TransactionException;
import vn.com.loyalty.core.service.internal.*;
import vn.com.loyalty.core.service.internal.impl.RankService;
import vn.com.loyalty.core.thirdparty.service.CmsWebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@KafkaListener(topics = Constants.KafkaConstants.TRANSACTION_TOPIC, groupId = Constants.KafkaConstants.TRANSACTION_GROUP)
@Slf4j
@RequiredArgsConstructor
public class TransactionListener {

    private final ObjectMapper objectMapper;
    private final TransactionMessageService transactionMessageService;
    private final TransactionService transactionService;
    private final RankService rankService;
    private final RedisOperation redisOperation;
    private final CmsWebClient cmsWebClient;

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

            TransactionEntity transactionEntity = TransactionEntity.builder()
                    .customerCode(transactionMessage.getCustomerCode())
                    .transactionTime(transactionMessage.getTransactionTime())
                    .transactionId(transactionMessage.getTransactionId())
                    .transactionValue(transactionMessage.getData().getTransactionValue())
                    .transactionType(TransactionType.valueOf(transactionMessage.getTransactionType()))
                    .epointGain(epointGain)
                    .rpointGain(rpointGain)
                    .epointSpend(epointSpend)
                    .build();

            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> transactionService.saveTransaction(transactionEntity)),
                    CompletableFuture.runAsync(() -> this.savePointToRedis(transactionEntity)),
                    CompletableFuture.runAsync(() -> this.rankCheck(transactionEntity)))
                    .exceptionally(throwable -> {
                        throw new TransactionException(throwable.getMessage());
                    }).join();

            redisOperation.commit();
        } catch (Exception e) {
            redisOperation.rollback();
            throw new TransactionException(e.getMessage());
        }

    }

    private void savePointToRedis(TransactionEntity transaction) {

        String epointKey = redisOperation.genEpointKey(transaction.getCustomerCode());

        BigDecimal epoint = redisOperation.hasValue(epointKey) ? redisOperation.getValue(epointKey, BigDecimal.class) : BigDecimal.ZERO;

        if (epoint.compareTo(transaction.getEpointSpend()) < 0) {
            throw new CustomerPointException(transaction.getTransactionId(), transaction.getCustomerCode(), epoint, transaction.getEpointSpend());
        }

        redisOperation.setValue(epointKey, epoint.add(transaction.getEpointGain()).subtract(transaction.getEpointSpend()).toString());

        String rpointKey = redisOperation.genRpointKey(transaction.getCustomerCode());
        if (redisOperation.hasValue(rpointKey)) {
            BigDecimal rpoint = redisOperation.getValue(epointKey, BigDecimal.class);
            redisOperation.setValue(epointKey, rpoint.add(transaction.getRpointGain()).toString());
        } else {
            redisOperation.setValue(rpointKey, transaction.getRpointGain().toString());
        }
    }

    private void rankCheck (TransactionEntity transaction) {

        String customerCode = transaction.getCustomerCode();
        BigDecimal rpoint = redisOperation.getValue(redisOperation.genRpointKey(customerCode), BigDecimal.class);

        List<RankResponse> rankResponseList = new ArrayList<>();

        try {
            rankResponseList = (List<RankResponse>) redisOperation.getValuesMatchPrefix(Constants.RedisConstants.RANK_DIR, RankResponse.class);
        } catch (Exception e) {
            rankResponseList = cmsWebClient.receiveRankList().getDataList();
        } finally {
            rankResponseList.sort(Comparator.comparing(RankResponse::getRequirePoint,  Comparator.reverseOrder()));
        }

        String rankOfPoint = rankService.getRankByPoint(rpoint, rankResponseList);

        /** TODO :
         * update rank for customer if rankOfPoint != null (consider need to cache rank of customer or not)
         * if not cache need to call cms service to get current rank of customer
         * consider cache list rank sorted or not
         * */

        if (StringUtils.hasText(rankOfPoint)) {

        }

    }
}
