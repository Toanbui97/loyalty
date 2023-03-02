package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.dto.message.TransactionMessage;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;

import java.math.BigDecimal;

public interface TransactionService {
    TransactionEntity saveTransaction(TransactionEntity transactionEntity);
    BigDecimal calculateEpointGain(TransactionMessage transactionMessage);
    BigDecimal calculateRpointGain(TransactionMessage transactionMessage);
}
