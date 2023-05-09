package vn.com.loyalty.transaction.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionMessageReq;
import vn.com.loyalty.core.dto.message.TransactionMessageRes;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;
import vn.com.loyalty.transaction.dto.VoucherMessage;
import vn.com.loyalty.transaction.service.OrchestrationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrchestrationController {

    private final ResponseFactory responseFactory;
    private final OrchestrationService orchestrationService;

    @PostMapping("/orchestration/transaction")
    public ResponseEntity<BodyResponse<TransactionMessageRes>> transactionOrchestration(@RequestBody BodyRequest<TransactionMessageReq> request) {
        return responseFactory.success(orchestrationService.processTransactionOrchestration(request.getData()));
    }

    @PostMapping("/orchestration/voucher")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> voucherOrchestration(@RequestBody BodyRequest<VoucherMessage> req) {

        return responseFactory.success(orchestrationService.processVoucherOrchestration(req.getData()));
    }
}
