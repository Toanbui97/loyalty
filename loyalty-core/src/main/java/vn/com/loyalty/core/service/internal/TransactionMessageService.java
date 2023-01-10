package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;

public interface TransactionMessageService {
    TransactionMessageEntity saveMessage(TransactionMessageEntity transactionIncome);
}
