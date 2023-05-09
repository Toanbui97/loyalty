package vn.com.loyalty.transaction.service;

import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.dto.message.TransactionMessageRes;
import vn.com.loyalty.core.dto.request.VoucherMessageReq;

public interface OrchestrationService {
    TransactionMessageRes processTransactionOrchestration(TransactionMessageReq message);

    OrchestrationMessage processVoucherOrchestration(VoucherMessageReq voucherMessageReq);
}
