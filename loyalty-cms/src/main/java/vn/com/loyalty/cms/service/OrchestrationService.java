package vn.com.loyalty.cms.service;

import vn.com.loyalty.cms.dto.TransactionMessage;
import vn.com.loyalty.cms.dto.VoucherOrchestrationMessage;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;

public interface OrchestrationService {
    OrchestrationMessage processOrchestrationTransaction(TransactionOrchestrationMessage req);
    OrchestrationMessage rollbackOrchestrationTransaction(TransactionOrchestrationMessage req);
    OrchestrationMessage processOrchestrationBuyVoucher(VoucherOrchestrationMessage req);
    OrchestrationMessage rollbackOrchestrationBuyVoucher(VoucherOrchestrationMessage req);
}
