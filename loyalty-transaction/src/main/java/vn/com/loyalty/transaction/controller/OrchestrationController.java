package vn.com.loyalty.transaction.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionMessage;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;
import vn.com.loyalty.transaction.dto.VoucherMessage;
import vn.com.loyalty.transaction.kafka.listener.TransactionListener;
import vn.com.loyalty.transaction.service.OrchestrationService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrchestrationController {

    private final ResponseFactory responseFactory;
    private final OrchestrationService orchestrationService;
    private final TransactionListener transactionListener;

    @PostMapping("/orchestration/transaction")
    public ResponseEntity<?> transactionOrchestration(@RequestBody BodyRequest<TransactionMessage> request) throws JsonProcessingException {
        transactionListener.processHttpTransaction(request.getData());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/orchestration/voucher")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> voucherOrchestration(@RequestBody BodyRequest<VoucherMessage> req) throws JsonProcessingException {

        return responseFactory.success(orchestrationService.processVoucherOrchestration(req.getData()));
    }
}