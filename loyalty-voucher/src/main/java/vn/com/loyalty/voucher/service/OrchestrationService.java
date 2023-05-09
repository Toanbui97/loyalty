package vn.com.loyalty.voucher.service;

import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationReq;

public interface OrchestrationService {
    OrchestrationMessage processOrchestrationTransaction(TransactionOrchestrationReq data);

    OrchestrationMessage rollbackOrchestrationTransaction(OrchestrationMessage req);
}
