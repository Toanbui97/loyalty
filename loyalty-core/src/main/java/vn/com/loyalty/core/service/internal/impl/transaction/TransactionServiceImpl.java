package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.dto.message.TransactionMessageDTO;
import vn.com.loyalty.core.entity.transaction.EpointGainEntity;
import vn.com.loyalty.core.entity.transaction.EpointSpendEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.service.internal.TransactionService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final EpointGainRepository epointGainRepository;
    private final EpointSpendRepository epointSpendRepository;

    @Override
    public TransactionEntity saveTransaction(TransactionEntity transactionEntity) {

        if (transactionEntity.getEpointGain()!= null &&transactionEntity.getEpointGain().compareTo(BigDecimal.ZERO) > 0) {
            epointGainRepository.save(EpointGainEntity.builder()
                    .customerCode(transactionEntity.getCustomerCode())
                    .epointGain(transactionEntity.getEpointGain())
                    .day(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))
                    .build());
        }

        if (transactionEntity.getEpointSpend()!= null &&transactionEntity.getEpointSpend().compareTo(BigDecimal.ZERO) > 0) {
            epointSpendRepository.save(EpointSpendEntity.builder()
                    .customerCode(transactionEntity.getCustomerCode())
                    .epointSpend(transactionEntity.getEpointSpend())
                    .day(LocalDateTime.now().truncatedTo(ChronoUnit.DAYS))
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
