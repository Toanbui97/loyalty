package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.repository.*;
import vn.com.loyalty.core.service.internal.TransactionService;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public TransactionEntity saveTransaction(TransactionEntity transactionEntity) {
        return transactionRepository.save(transactionEntity);
    }

    @Override
    public BigDecimal calculateEpointGain(TransactionMessageReq transactionMessageReq) {
        return transactionMessageReq.getData().getTransactionValue().divide(BigDecimal.valueOf(100));
    }


    @Override
    public BigDecimal calculateRpointGain(TransactionMessageReq transactionMessageReq) {
        return transactionMessageReq.getData().getTransactionValue().divide(BigDecimal.valueOf(50));
    }

}
