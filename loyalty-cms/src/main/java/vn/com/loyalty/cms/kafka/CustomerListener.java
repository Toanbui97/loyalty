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
import vn.com.loyalty.core.entity.cms.RpointEntity;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.repository.MasterDataRepository;
import vn.com.loyalty.core.repository.RpointRepository;
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

    @KafkaListener(topics = Constants.KafkaConstants.POINT_TOPIC, groupId = Constants.KafkaConstants.POINT_GROUP)
    public void pointListener(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {

        try {

            CustomerMessageDTO messageDTO = objectMapper.readValue(message, CustomerMessageDTO.class);
            CustomerEntity customerEntity = customerRepository.findByCustomerCode(messageDTO.getCustomerCode())
                    .orElse(CustomerEntity.builder().customerCode(messageDTO.getCustomerCode()).build());


            if (redisOperation.hasValue(redisOperation.genEpointKey(customerEntity.getCustomerCode()))) {
                customerEntity.setEpoint(redisOperation.getValue(
                        redisOperation.genEpointKey(messageDTO.getCustomerCode()), BigDecimal.class
                ));
            }

            if (messageDTO.getRpoint() != null) {
                rpointRepository.save(RpointEntity.builder()
                        .customerCode(messageDTO.getCustomerCode())
                        .rpoint(messageDTO.getRpoint())
                        .transactionDay(LocalDate.now())
                        .transactionId(messageDTO.getTransactionId())
                        .build());

                customerEntity.setRpoint(customerEntity.getRpoint().add(messageDTO.getRpoint()));

                String rankCode = rankService.getRankByPoint(messageDTO.getEpoint());

                if (!customerEntity.getRankCode().equals(rankCode)) {
                    long monthRankExpire = Long.parseLong(masterDataRepository.findByKey(Constants.MasterDataKey.RANK_EXPIRE_TIME).getValue());
                    customerEntity.setRankCode(rankCode);
                    customerEntity.setRankExpired(customerEntity.getRankExpired().plusMonths(monthRankExpire));
                }
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
