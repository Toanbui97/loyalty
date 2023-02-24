package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.dto.message.TransactionMessageDTO;
import vn.com.loyalty.core.entity.cms.RpointEntity;
import vn.com.loyalty.core.entity.transaction.EpointGainEntity;
import vn.com.loyalty.core.entity.transaction.EpointSpendEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.service.internal.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final EpointGainRepository epointGainRepository;
    private final EpointSpendRepository epointSpendRepository;
    private final RpointRepository rpointRepository;

    @Override
    public TransactionEntity saveTransaction(TransactionEntity transactionEntity) {

        LocalDate today = LocalDate.now();

        // save epoint gain
        if (transactionEntity.getEpointGain()!= null && transactionEntity.getEpointGain().compareTo(BigDecimal.ZERO) > 0) {
            epointGainRepository.save(EpointGainEntity.builder()
                    .transactionId(transactionEntity.getTransactionId())
                    .customerCode(transactionEntity.getCustomerCode())
                    .epoint(transactionEntity.getEpointGain())
                    .transactionDay(today)
                    .build());
        }

        // save epoint spend
        if (transactionEntity.getEpointSpend()!= null && transactionEntity.getEpointSpend().compareTo(BigDecimal.ZERO) > 0) {
            epointSpendRepository.save(EpointSpendEntity.builder()
                    .transactionId(transactionEntity.getTransactionId())
                    .customerCode(transactionEntity.getCustomerCode())
                    .epoint(transactionEntity.getEpointSpend())
                    .transactionDay(today)
                    .build());
        }

        // save rpoint gain
        if (transactionEntity.getRpointGain() != null && transactionEntity.getRpointGain().compareTo(BigDecimal.ZERO) > 0) {
            rpointRepository.save(RpointEntity.builder()
                    .transactionId(transactionEntity.getTransactionId())
                    .customerCode(transactionEntity.getCustomerCode())
                    .rpoint(transactionEntity.getRpointGain())
                    .transactionDay(today)
                    .build());
        }

        return transactionRepository.save(transactionEntity);
    }

    @Override
    public BigDecimal calculateEpointGain(TransactionMessageDTO transactionMessage) {
        return transactionMessage.getData().getTransactionValue().divide(BigDecimal.valueOf(100));
    }


    @Override
    public BigDecimal calculateRpointGain(TransactionMessageDTO transactionMessage) {
        return transactionMessage.getData().getTransactionValue().divide(BigDecimal.valueOf(50));
    }

}
