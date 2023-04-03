package vn.com.loyalty.voucher.service;

import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;

public interface OrchestrationService {
    OrchestrationMessage processOrchestrationTransaction(TransactionOrchestrationMessage data);

    OrchestrationMessage rollbackOrchestrationTransaction(OrchestrationMessage req);
}
