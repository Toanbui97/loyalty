package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;

import java.math.BigDecimal;

public interface TransactionService {
    TransactionEntity saveTransaction(TransactionEntity transactionEntity);
    BigDecimal calculateEpointGain(TransactionMessageReq transactionMessageReq);
    BigDecimal calculateRpointGain(TransactionMessageReq transactionMessageReq);
}
