package vn.com.loyalty.cms.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.message.CustomerMessageDTO;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;
import vn.com.loyalty.core.entity.cms.EpointSpendEntity;
import vn.com.loyalty.core.entity.cms.RpointEntity;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.impl.RankService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerListener {

    private final ObjectMapper objectMapper;
    private final RankService rankService;
    private final CustomerRepository customerRepository;
    private final MasterDataRepository masterDataRepository;
    private final RpointRepository rpointRepository;
    private final RedisOperation redisOperation;
    private final EpointGainRepository epointGainRepository;
    private final EpointSpendRepository epointSpendRepository;

    @KafkaListener(topics = Constants.KafkaConstants.POINT_TOPIC, groupId = Constants.KafkaConstants.POINT_GROUP)
    public void pointListener(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        LocalDate today = LocalDate.now();

        try {

            CustomerMessageDTO messageDTO = objectMapper.readValue(message, CustomerMessageDTO.class);
            CustomerEntity customerEntity = customerRepository.findByCustomerCode(messageDTO.getCustomerCode())
                    .orElse(CustomerEntity.builder().customerCode(messageDTO.getCustomerCode()).build());

            if (redisOperation.hasValue(redisOperation.genEpointKey(customerEntity.getCustomerCode()))) {
                customerEntity.setEpoint(redisOperation.getValue(
                        redisOperation.genEpointKey(messageDTO.getCustomerCode()), BigDecimal.class
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
                long monthRankExpire = Long.parseLong(masterDataRepository.findByKey(Constants.MasterDataKey.RANK_EXPIRE_TIME).getValue());
                customerEntity.setRankCode(rankCode);
                customerEntity.setRankExpired(customerEntity.getRankExpired().plusMonths(monthRankExpire));
            }

            // save rpoint
            if (messageDTO.getRpointGain() != null && messageDTO.getRpointGain().compareTo(BigDecimal.ZERO) > 0) {
                rpointRepository.save(RpointEntity.builder()
                        .customerCode(messageDTO.getCustomerCode())
                        .rpoint(messageDTO.getRpointGain())
                        .transactionDay(LocalDate.now())
                        .transactionId(messageDTO.getTransactionId())
                        .build());
            }

            // save epoint gain
            if (messageDTO.getEpointGain() != null && messageDTO.getEpointGain().compareTo(BigDecimal.ZERO) > 0) {
                epointGainRepository.save(EpointGainEntity.builder()
                        .transactionId(messageDTO.getTransactionId())
                        .customerCode(messageDTO.getCustomerCode())
                        .epoint(messageDTO.getEpointGain())
                        .transactionDay(today)
                        .build());
            }

            // save epoint spend
            if (messageDTO.getEpointSpend()!= null && messageDTO.getEpointSpend().compareTo(BigDecimal.ZERO) > 0) {
                epointSpendRepository.save(EpointSpendEntity.builder()
                        .transactionId(messageDTO.getTransactionId())
                        .customerCode(messageDTO.getCustomerCode())
                        .epoint(messageDTO.getEpointSpend())
                        .transactionDay(today)
                        .build());
            }

            if (messageDTO.getActiveVoucher() != null) {
                customerEntity.setActiveVoucher(messageDTO.getActiveVoucher());
            }


            customerRepository.save(customerEntity);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
