package vn.com.loyalty.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.com.loyalty.cms.dto.VoucherOrchestrationMessage;
import vn.com.loyalty.cms.service.OrchestrationService;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.service.internal.CustomerService;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class OrchestrationController {

    private final CustomerService customerService;
    private final ResponseFactory responseFactory;
    private final OrchestrationService orchestrationService;

    @PostMapping("/processOrchestrationTransaction")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> processOrchestrationTransaction(@RequestBody BodyRequest<TransactionOrchestrationMessage> req) {
        return responseFactory.success(orchestrationService.processOrchestrationTransaction(req.getData()));
    }

    @PostMapping("/rollbackOrchestrationTransaction")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> rollbackOrchestrationTransaction(@RequestBody BodyRequest<TransactionOrchestrationMessage> req) {
        return responseFactory.success(orchestrationService.rollbackOrchestrationTransaction(req.getData()));
    }

    @PostMapping("/processBuyVoucherOrchestration")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> processBuyVoucherOrchestration(@RequestBody BodyRequest<VoucherOrchestrationMessage> req) {
        return responseFactory.success(orchestrationService.processOrchestrationBuyVoucher(req.getData()));
    }
}
