//package vn.com.loyalty.cms.kafka;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.messaging.MessageHeaders;
//import org.springframework.messaging.handler.annotation.Headers;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import vn.com.loyalty.core.constant.Constants;
//import vn.com.loyalty.core.dto.message.OrchestrationMessage;
//import vn.com.loyalty.core.entity.cms.*;
//import vn.com.loyalty.core.exception.OrchestrationException;
//import vn.com.loyalty.core.repository.*;
//import vn.com.loyalty.core.service.internal.CustomerService;
//import vn.com.loyalty.core.service.internal.MasterDataService;
//import vn.com.loyalty.core.service.internal.impl.RankService;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class CMSKafkaListener {
//
//    private final ObjectMapper objectMapper;
//    private final RankService rankService;
//    private final CustomerRepository customerRepository;
//    private final MasterDataService masterDataService;
//    private final RpointRepository rpointRepository;
//    private final EpointGainRepository epointGainRepository;
//    private final EpointSpendRepository epointSpendRepository;
//    private final CustomerService customerService;
//
//    @Transactional(rollbackFor = Exception.class)
//    @KafkaListener(topics = Constants.KafkaConstants.POINT_TOPIC, groupId = Constants.KafkaConstants.POINT_GROUP)
//    public void cmsTransactionListener(@Payload String payload, @Headers MessageHeaders headers) throws JsonProcessingException {
//        LocalDate today = LocalDate.now();
//        OrchestrationMessage message = objectMapper.readValue(payload, OrchestrationMessage.class);
//        CustomerEntity customerEntity = customerRepository.findByCustomerCode(message.getCustomerCode())
//                .orElse(CustomerEntity.builder().customerCode(message.getCustomerCode()).build());
//
//        // save epoint gain
//        if (message.getCmsData().getEpointGain() != null && message.getCmsData().getEpointGain().compareTo(BigDecimal.ZERO) > 0) {
//            customerEntity.setEpoint(customerEntity.getEpoint().add(message.getCmsData().getEpointGain()));
//            epointGainRepository.save(EpointGainEntity.builder()
//                    .transactionId(message.getTransactionId())
//                    .customerCode(message.getCustomerCode())
//                    .epoint(message.getCmsData().getEpointGain())
//                    .expireDay(today.plusMonths(masterDataService.getValue(Constants.MasterDataKey.EPOINT_EXPIRE_TIME, Long.class)))
//                    .transactionDay(today)
//                    .build());
//        }
//
//        // save epoint spend
//        if (message.getCmsData().getEpointSpend() != null && message.getCmsData().getEpointSpend().compareTo(BigDecimal.ZERO) > 0) {
//            epointSpendRepository.save(EpointSpendEntity.builder()
//                    .transactionId(message.getTransactionId())
//                    .customerCode(message.getCustomerCode())
//                    .epoint(message.getCmsData().getEpointSpend())
//                    .transactionDay(today)
//                    .build());
//        }
//
//        // save rpoint
//        if (message.getCmsData().getRpointGain() != null && message.getCmsData().getRpointGain().compareTo(BigDecimal.ZERO) > 0) {
//            customerEntity.setRpoint(customerEntity.getRpoint().add(message.getCmsData().getRpointGain()));
//            rpointRepository.save(RpointEntity.builder()
//                    .customerCode(message.getCustomerCode())
//                    .rpoint(message.getCmsData().getRpointGain())
//                    .transactionId(message.getTransactionId())
//                    .build());
//        }
//
//        // check rank
//        // case up rank
//        RankEntity rank = rankService.getRankByPoint(customerEntity.getRpoint());
//        if (!customerEntity.getRankCode().equals(rank.getRankCode())) {
//            long monthRankExpire = masterDataService.getValue(Constants.MasterDataKey.RANK_EXPIRE_TIME, Long.class);
//            customerEntity.setRankCode(rank.getRankCode());
//            customerEntity.setRankExpired(customerEntity.getRankExpired().plusMonths(monthRankExpire));
//            customerService.saveHistoryRankUp(customerEntity);
//        }
//
//        if (message.getCmsData().getActiveVoucher() != null) {
//            customerEntity.setActiveVoucher(message.getCmsData().getActiveVoucher());
//        }
//
//        customerRepository.save(customerEntity);
//    }
//
//}
