package vn.com.loyalty.cms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.com.loyalty.cms.dto.TransactionMessage;
import vn.com.loyalty.cms.dto.TransactionResponse;
import vn.com.loyalty.cms.dto.VoucherMessage;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;
import vn.com.loyalty.core.entity.cms.*;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.repository.CustomerRepository;
import vn.com.loyalty.core.repository.EpointGainRepository;
import vn.com.loyalty.core.repository.EpointSpendRepository;
import vn.com.loyalty.core.repository.RpointRepository;
import vn.com.loyalty.core.service.internal.CustomerService;
import vn.com.loyalty.core.service.internal.MasterDataService;
import vn.com.loyalty.core.service.internal.RankHistoryService;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.service.internal.impl.RankService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

    private final ObjectMapper objectMapper;
    private final RankService rankService;
    private final CustomerRepository customerRepository;
    private final MasterDataService masterDataService;
    private final RpointRepository rpointRepository;
    private final EpointGainRepository epointGainRepository;
    private final EpointSpendRepository epointSpendRepository;
    private final CustomerService customerService;
    private final RankHistoryService rankHistoryService;
    private final RedisOperation redisOperation;

    @Override
    public OrchestrationMessage processOrchestrationTransaction(TransactionOrchestrationMessage req) {
        LocalDate today = LocalDate.now();

        CustomerEntity customerEntity = customerRepository.findByCustomerCode(req.getCustomerCode())
                .orElse(CustomerEntity.builder().customerCode(req.getCustomerCode()).build());

        // save epoint gain
        if (req.getEpointGain() != null && req.getEpointGain().compareTo(BigDecimal.ZERO) > 0) {
            customerEntity.setEpoint(customerEntity.getEpoint().add(req.getEpointGain()));
            epointGainRepository.save(EpointGainEntity.builder()
                    .transactionId(req.getTransactionId())
                    .customerCode(req.getCustomerCode())
                    .epoint(req.getEpointGain())
                    .expireDay(today.plusMonths(masterDataService.getValue(Constants.MasterDataKey.EPOINT_EXPIRE_TIME, Long.class)))
                    .transactionDay(today)
                    .build());
        }

        // save epoint spend
        if (req.getEpointSpend() != null && req.getEpointSpend().compareTo(BigDecimal.ZERO) > 0) {
            epointSpendRepository.save(EpointSpendEntity.builder()
                    .transactionId(req.getTransactionId())
                    .customerCode(req.getCustomerCode())
                    .epoint(req.getEpointSpend())
                    .transactionDay(today)
                    .build());
        }

        // save rpoint
        if (req.getRpointGain() != null && req.getRpointGain().compareTo(BigDecimal.ZERO) > 0) {
            customerEntity.setRpoint(customerEntity.getRpoint().add(req.getRpointGain()));
            rpointRepository.save(RpointEntity.builder()
                    .customerCode(req.getCustomerCode())
                    .rpoint(req.getRpointGain())
                    .transactionId(req.getTransactionId())
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

        if (!CollectionUtils.isEmpty(req.getVoucherDetailCodeList())) {
            customerEntity.setActiveVoucher(customerEntity.getActiveVoucher() - req.getVoucherDetailCodeList().size());
        }

        customerRepository.save(customerEntity);

        return req;
    }

    @Override
    @Transactional
    public OrchestrationMessage rollbackOrchestrationTransaction(TransactionOrchestrationMessage req) {

        CustomerEntity customerEntity = customerRepository.findByCustomerCode(req.getCustomerCode())
                .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, req.getCustomerCode()));

        EpointGainEntity epointGainEntity = epointGainRepository.findByTransactionId(req.getTransactionId()).orElse(null);
        EpointSpendEntity epointSpendEntity = epointSpendRepository.findByTransactionId(req.getTransactionId()).orElse(null);
        RpointEntity rpointEntity = rpointRepository.findByTransactionId(req.getTransactionId()).orElse(null);

        BigDecimal epointGain = BigDecimal.ZERO;
        BigDecimal epointSpend = BigDecimal.ZERO;
        BigDecimal rpointGain = BigDecimal.ZERO;

        // rollback EpointGain
        if (epointGainEntity != null) {
            epointGainEntity.setStatus(PointStatus.ROLLBACK);
            epointGain = epointGainEntity.getEpoint();
            epointGainRepository.save(epointGainEntity);
        }
        if (epointSpendEntity != null) {
            epointSpendEntity.setStatus(PointStatus.ROLLBACK);
            epointSpend = epointSpendEntity.getEpoint();
            epointSpendRepository.save(epointSpendEntity);
        }
        if (rpointEntity != null) {
            rpointEntity.setStatus(PointStatus.ROLLBACK);
            rpointGain = rpointEntity.getRpoint();
            rpointRepository.save(rpointEntity);
        }

        customerEntity.setEpoint(customerEntity.getEpoint().add(epointGain.subtract(epointSpend)));
        customerEntity.setRpoint(customerEntity.getRpoint().subtract(rpointGain));
        customerEntity.setActiveVoucher(customerEntity.getActiveVoucher() + req.getVoucherDetailCodeList().size());

        RankEntity rankEntity = rankService.getRankByPoint(customerEntity.getRpoint());
        if (!rankEntity.getRankCode().equals(customerEntity.getRankCode())) {
            rankService.getInferiorityRank(rankEntity);
            RankHistoryEntity rankHistoryEntity = rankHistoryService.getForRollback(req.getCustomerCode());
            customerEntity.setRankCode(rankHistoryEntity.getRankCode());
            customerEntity.setRankExpired(rankHistoryEntity.getUpdatedDate().minusMonths(masterDataService.getValue(Constants.MasterDataKey.RANK_EXPIRE_TIME, Long.class)));
        }

        redisOperation.setValue(redisOperation.genEpointKey(customerEntity.getCustomerCode()), customerEntity.getEpoint());
        redisOperation.setValue(redisOperation.genRpointKey(customerEntity.getCustomerCode()), customerEntity.getRpoint());

        customerRepository.save(customerEntity);

        return req;
    }

    @Override
    @Transactional
    public OrchestrationMessage processOrchestrationBuyVoucher(VoucherMessage req) {

        CustomerEntity customerEntity = customerRepository.findByCustomerCode(req.getCustomerCode())
                .orElseThrow(() -> new ResourceNotFoundException(CustomerEntity.class, req.getCustomerCode()));

        // save epoint spend
        epointSpendRepository.save(EpointSpendEntity.builder()
                .transactionId(req.getTransactionId())
                .customerCode(req.getCustomerCode())
                .epoint(req.getEpointSpend())
                .transactionDay(LocalDate.now())
                .build());

        customerEntity.setEpoint(customerEntity.getEpoint().subtract(req.getEpointSpend()));
        customerEntity.setActiveVoucher(customerEntity.getActiveVoucher() + req.getActiveVoucher());

        customerRepository.save(customerEntity);

        return req;
    }

    @Override
    public TransactionResponse rollbackOrchestrationBuyVoucher(TransactionMessage req) {
        return null;
    }
}
