package vn.com.loyalty.cms.service;

import vn.com.loyalty.cms.dto.VoucherOrchestrationMessage;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationReq;

public interface OrchestrationService {
    OrchestrationMessage processOrchestrationTransaction(TransactionOrchestrationReq req);
    OrchestrationMessage rollbackOrchestrationTransaction(TransactionOrchestrationReq req);
    OrchestrationMessage processOrchestrationBuyVoucher(VoucherOrchestrationMessage req);
    OrchestrationMessage rollbackOrchestrationBuyVoucher(VoucherOrchestrationMessage req);
}
