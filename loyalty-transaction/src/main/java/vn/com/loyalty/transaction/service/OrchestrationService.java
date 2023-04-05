package vn.com.loyalty.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionMessage;
import vn.com.loyalty.transaction.dto.VoucherMessage;

public interface OrchestrationService {
    TransactionMessage processTransactionOrchestration(TransactionMessage message);

    OrchestrationMessage processVoucherOrchestration(VoucherMessage voucherMessage);
}
