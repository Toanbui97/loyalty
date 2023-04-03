package vn.com.loyalty.voucher.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;
import vn.com.loyalty.voucher.dto.VoucherMessage;
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
    public ResponseEntity<BodyResponse<OrchestrationMessage>> processOrchestrationTransaction(@RequestBody BodyRequest<TransactionOrchestrationMessage> req) {
        return responseFactory.success(orchestrationService.processOrchestrationTransaction(req.getData()));
    }

    @PostMapping("/rollbackOrchestrationTransaction")
    public ResponseEntity<BodyResponse<OrchestrationMessage>> rollbackOrchestrationTransaction(@RequestBody BodyRequest<OrchestrationMessage> req) {
        return responseFactory.success(orchestrationService.rollbackOrchestrationTransaction(req.getData()));
    }

    @PostMapping("/processBuyVoucherOrchestration")
    public ResponseEntity<BodyResponse<VoucherResponse>> performBuyVoucher(@RequestBody BodyRequest<VoucherMessage> req,
                                                                           @PathVariable String voucherCode) {
        return responseFactory.success(voucherService.processOrchestrationBuyVoucher(req.getData()));
    }

    @PostMapping("/receiveVoucherList/{customerCode}")
    public ResponseEntity<BodyResponse<VoucherResponse>> receiveVoucherListOfCustomer(@RequestBody BodyRequest<VoucherRequest> request
            , @PathVariable String customerCode, @PageableDefault Pageable page) {
        return responseFactory.success(voucherService.getVoucherListOfCustomer(customerCode, page));
    }
}
