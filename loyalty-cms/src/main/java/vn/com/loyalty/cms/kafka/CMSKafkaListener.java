package vn.com.loyalty.cms.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.dto.message.CustomerMessage;
import vn.com.loyalty.core.entity.MasterDataEntity;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;
import vn.com.loyalty.core.entity.cms.EpointSpendEntity;
import vn.com.loyalty.core.entity.cms.RpointEntity;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.service.internal.MasterDataService;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.impl.RankService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class CMSKafkaListener {

    private final ObjectMapper objectMapper;
    private final RankService rankService;
    private final CustomerRepository customerRepository;
    private final MasterDataService masterDataService;
    private final RpointRepository rpointRepository;
    private final RedisOperation redisOperation;
    private final EpointGainRepository epointGainRepository;
    private final EpointSpendRepository epointSpendRepository;

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = Constants.KafkaConstants.POINT_TOPIC, groupId = Constants.KafkaConstants.POINT_GROUP)
    public void cmsTransactionListener(@Payload String payload, @Headers MessageHeaders headers) {
        LocalDate today = LocalDate.now();

        try {
            CustomerMessage message = objectMapper.readValue(payload, CustomerMessage.class);
            CustomerEntity customerEntity = customerRepository.findByCustomerCode(message.getCustomerCode())
                    .orElse(CustomerEntity.builder().customerCode(message.getCustomerCode()).build());

            if (redisOperation.hasValue(redisOperation.genEpointKey(customerEntity.getCustomerCode()))) {
                customerEntity.setEpoint(redisOperation.getValue(
                        redisOperation.genEpointKey(message.getCustomerCode()), BigDecimal.class
                ));
            }

            if (redisOperation.hasValue(redisOperation.genRpointKey(customerEntity.getCustomerCode()))) {
                customerEntity.setRpoint(redisOperation.getValue(
                        redisOperation.genRpointKey(customerEntity.getCustomerCode()), BigDecimal.class
                ));
            }

            // check rank
            String rankCode = rankService.getRankByPoint(customerEntity.getRpoint());
            if (!customerEntity.getRankCode().equals(rankCode)) {
                long monthRankExpire = masterDataService.getValue(Constants.MasterDataKey.RANK_EXPIRE_TIME, Long.class);
                customerEntity.setRankCode(rankCode);
                customerEntity.setRankExpired(customerEntity.getRankExpired().plusMonths(monthRankExpire));
            }

            // save rpoint
            if (message.getData().getRpointGain() != null && message.getData().getRpointGain().compareTo(BigDecimal.ZERO) > 0) {
                rpointRepository.save(RpointEntity.builder()
                        .customerCode(message.getCustomerCode())
                        .rpoint(message.getData().getRpointGain())
                        .transactionId(message.getTransactionId())
                        .build());
            }

            // save epoint gain
            if (message.getData().getEpointGain() != null && message.getData().getEpointGain().compareTo(BigDecimal.ZERO) > 0) {
                epointGainRepository.save(EpointGainEntity.builder()
                        .transactionId(message.getTransactionId())
                        .customerCode (message.getCustomerCode())
                        .epoint(message.getData().getEpointGain())
                        .expireDay(today.plusMonths(masterDataService.getValue(Constants.MasterDataKey.EPOINT_EXPIRE_TIME, Long.class)))
                        .transactionDay(today)
                        .build());
            }

            // save epoint spend
            if (message.getData().getEpointSpend()!= null && message.getData().getEpointSpend().compareTo(BigDecimal.ZERO) > 0) {
                epointSpendRepository.save(EpointSpendEntity.builder()
                        .transactionId(message.getTransactionId())
                        .customerCode(message.getCustomerCode())
                        .epoint(message.getData().getEpointSpend())
                        .transactionDay(today)
                        .build());
            }

            if (message.getData().getActiveVoucher() != null) {
                customerEntity.setActiveVoucher(message.getData().getActiveVoucher());
            }

            customerRepository.save(customerEntity);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
