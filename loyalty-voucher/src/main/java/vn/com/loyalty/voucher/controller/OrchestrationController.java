package vn.com.loyalty.voucher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationReq;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;
import vn.com.loyalty.voucher.dto.VoucherOrchestrationMessage;
import vn.com.loyalty.voucher.service.OrchestrationService;
import vn.com.loyalty.voucher.service.VoucherDetailService;
import vn.com.loyalty.voucher.service.VoucherService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class OrchestrationController {

    private final VoucherService voucherService;
    private final ResponseFactory responseFactory;
    private final VoucherDetailService voucherDetailService;
    private final OrchestrationService orchestrationService;

    @PostMapping("/processOrchestrationTransaction")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> processOrchestrationTransaction(@RequestBody BodyRequest<TransactionOrchestrationReq> req) {
        return responseFactory.success(orchestrationService.processOrchestrationTransaction(req.getData()));
    }

    @PostMapping("/rollbackOrchestrationTransaction")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> rollbackOrchestrationTransaction(@RequestBody BodyRequest<TransactionOrchestrationReq> req) {
        return responseFactory.success(orchestrationService.rollbackOrchestrationTransaction(req.getData()));
    }

    @PostMapping("/processBuyVoucherOrchestration")
    public ResponseEntity<BodyResponse<VoucherResponse>> processBuyVoucherOrchestration(@RequestBody BodyRequest<VoucherOrchestrationMessage> req) {
        return responseFactory.success(voucherService.processOrchestrationBuyVoucher(req.getData()));
    }

    @PostMapping("/rollbackBuyVoucherOrchestration")
    public ResponseEntity<BodyResponse<VoucherResponse>> rollbackBuyVoucherOrchestration(@RequestBody BodyRequest<VoucherOrchestrationMessage> req){
        return responseFactory.success(voucherService.rollbackOrchestrationBuyVoucher(req.getData()));
    }

}
