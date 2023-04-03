package vn.com.loyalty.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.transaction.dto.VoucherMessage;

public interface OrchestrationService {
    OrchestrationMessage processVoucherOrchestration(VoucherMessage voucherMessage) throws JsonProcessingException;
}
