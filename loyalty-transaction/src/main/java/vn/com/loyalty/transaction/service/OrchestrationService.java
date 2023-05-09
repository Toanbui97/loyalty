package vn.com.loyalty.transaction.service;

import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.dto.message.TransactionMessageRes;
import vn.com.loyalty.transaction.dto.VoucherMessage;

public interface OrchestrationService {
    TransactionMessageRes processTransactionOrchestration(TransactionMessageReq message);

    OrchestrationMessage processVoucherOrchestration(VoucherMessage voucherMessage);
}
