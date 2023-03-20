package vn.com.loyalty.cms.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.message.CustomerMessage;
import vn.com.loyalty.core.entity.cms.*;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.service.internal.CustomerService;
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
    private final EpointGainRepository epointGainRepository;
    private final EpointSpendRepository epointSpendRepository;
    private final CustomerService customerService;

    @Transactional(rollbackFor = Exception.class)
    @KafkaListener(topics = Constants.KafkaConstants.POINT_TOPIC, groupId = Constants.KafkaConstants.POINT_GROUP)
    public void cmsTransactionListener(@Payload String payload, @Headers MessageHeaders headers) throws JsonProcessingException {
        LocalDate today = LocalDate.now();

        CustomerMessage message = objectMapper.readValue(payload, CustomerMessage.class);
        CustomerEntity customerEntity = customerRepository.findByCustomerCode(message.getCustomerCode())
                .orElse(CustomerEntity.builder().customerCode(message.getCustomerCode()).build());

        // save epoint gain
        if (message.getData().getEpointGain() != null && message.getData().getEpointGain().compareTo(BigDecimal.ZERO) > 0) {
            customerEntity.setEpoint(customerEntity.getEpoint().add(message.getData().getEpointGain()));
            epointGainRepository.save(EpointGainEntity.builder()
                    .transactionId(message.getTransactionId())
                    .customerCode(message.getCustomerCode())
                    .epoint(message.getData().getEpointGain())
                    .expireDay(today.plusMonths(masterDataService.getValue(Constants.MasterDataKey.EPOINT_EXPIRE_TIME, Long.class)))
                    .transactionDay(today)
                    .build());
        }

        // save epoint spend
        if (message.getData().getEpointSpend() != null && message.getData().getEpointSpend().compareTo(BigDecimal.ZERO) > 0) {
            epointSpendRepository.save(EpointSpendEntity.builder()
                    .transactionId(message.getTransactionId())
                    .customerCode(message.getCustomerCode())
                    .epoint(message.getData().getEpointSpend())
                    .transactionDay(today)
                    .build());
        }

        // save rpoint
        if (message.getData().getRpointGain() != null && message.getData().getRpointGain().compareTo(BigDecimal.ZERO) > 0) {
            customerEntity.setRpoint(customerEntity.getRpoint().add(message.getData().getRpointGain()));
            rpointRepository.save(RpointEntity.builder()
                    .customerCode(message.getCustomerCode())
                    .rpoint(message.getData().getRpointGain())
                    .transactionId(message.getTransactionId())
                    .build());
        }

        // check rank
        // case up rank
        RankEntity rank = rankService.getRankByPoint(customerEntity.getRpoint());
        if (!customerEntity.getRankCode().equals(rank.getRankCode())) {
            long monthRankExpire = masterDataService.getValue(Constants.MasterDataKey.RANK_EXPIRE_TIME, Long.class);
            customerEntity.setRankCode(rank.getRankCode());
            customerEntity.setRankExpired(customerEntity.getRankExpired().plusMonths(monthRankExpire));
            customerService.saveHistoryRankUp(customerEntity);
        }

        if (message.getData().getActiveVoucher() != null) {
            customerEntity.setActiveVoucher(message.getData().getActiveVoucher());
        }

        customerRepository.save(customerEntity);
    }

}
