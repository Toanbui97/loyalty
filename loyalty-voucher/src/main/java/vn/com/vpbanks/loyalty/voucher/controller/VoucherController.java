package vn.com.vpbanks.loyalty.voucher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.vpbanks.loyalty.core.dto.request.BodyRequest;
import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherDetailService;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherService;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.vpbanks.loyalty.core.utils.factory.response.ResponseFactory;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;
    private final ResponseFactory responseFactory;
    private final VoucherDetailService voucherDetailService;

    @GetMapping("/receiveVoucherList")
    public ResponseEntity<BodyResponse<VoucherResponse>> receiveVoucherList() {
        return responseFactory.success(voucherService.getAllVoucher());
    }

    @PostMapping("/performCreateVoucher")
    public ResponseEntity<BodyResponse<VoucherResponse>> performCreateVoucher(@RequestBody BodyRequest<VoucherRequest> request) {
        return responseFactory.success(voucherService.createVoucher(request.getData()));
    }

    @GetMapping("/{voucherCode}/receiveVoucherDetailList")
    public ResponseEntity<BodyResponse<VoucherDetailResponse>> receiveVoucherDetailList(@PathVariable String voucherCode) {
        return responseFactory.success(voucherDetailService.getAllVoucherDetail(voucherCode));
    }

    @GetMapping("/{voucherCode}/receiveVoucherDetailInactiveList")
    public ResponseEntity<BodyResponse<VoucherDetailResponse>> receiveVoucherDetailInActiveList(@PathVariable String voucherCode) {
        return responseFactory.success(voucherDetailService.getVoucherDetailReadyForBuyList(voucherCode));
    }

    @PostMapping("/{voucherCode}/performBuyVoucher")
    public ResponseEntity<BodyResponse<VoucherResponse>> performBuyVoucher(@PathVariable String voucherCode) {
        return responseFactory.success(voucherService.buyVoucher(voucherCode));
    }

}
