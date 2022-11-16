package com.example.loyalty.voucher.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherDetailService;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherService;
import vn.com.vpbanks.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.vpbanks.loyalty.core.utils.factory.response.ResponseFactory;

@RestController
@RequestMapping("/api/v1/voucher")
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
    public ResponseEntity<BodyResponse<VoucherResponse>> performCreateVoucher(@RequestBody VoucherRequest request) {
        return responseFactory.success(voucherService.createVoucher(request));
    }

    @GetMapping("/receiveVoucherDetailList/{voucherCode}")
    public ResponseEntity<BodyResponse<VoucherDetailResponse>> receiveVoucherDetailList(@PathVariable String voucherCode) {
        return responseFactory.success(voucherDetailService.getAllVoucherDetail(voucherCode));
    }

    @PostMapping("/performActiveVoucher/{voucherCode}/")
    public ResponseEntity<BodyResponse<VoucherResponse>> performActiveVoucher(@PathVariable String voucherCode) {
        return responseFactory.success(voucherService.activeVoucher(voucherCode));
    }

}
