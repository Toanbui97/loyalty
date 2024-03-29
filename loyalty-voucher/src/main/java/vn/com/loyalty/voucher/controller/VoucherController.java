package vn.com.loyalty.voucher.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.voucher.dto.VoucherOrchestrationMessage;
import vn.com.loyalty.voucher.service.VoucherDetailService;
import vn.com.loyalty.voucher.service.VoucherService;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;
    private final ResponseFactory responseFactory;
    private final VoucherDetailService voucherDetailService;

    @PostMapping("/receiveVoucherList")
    public ResponseEntity<BodyResponse<VoucherResponse>> receiveVoucherList(@RequestBody BodyRequest<?> request
            , @PageableDefault(size = Integer.MAX_VALUE) Pageable page) {
        return responseFactory.success(voucherService.getVoucherList(page));
    }

    @PostMapping("/syncToRedis")
    public ResponseEntity<BodyResponse<VoucherResponse>> receiveVoucherList(@RequestBody BodyRequest<?> request) {
        return responseFactory.success(voucherService.syncDbWithRedis());
    }

    @PostMapping("/receiveVoucherList/{customerCode}")
    public ResponseEntity<BodyResponse<VoucherResponse>> receiveVoucherListOfCustomer(@RequestBody BodyRequest<VoucherOrchestrationMessage> request
            , @PathVariable String customerCode, @PageableDefault Pageable page) {
        return responseFactory.success(voucherService.getVoucherListOfCustomer(customerCode, page));
    }

    @PostMapping("/performCreateVoucher")
    public ResponseEntity<BodyResponse<VoucherResponse>> performCreateVoucher(@RequestBody BodyRequest<VoucherRequest> request) {
        return responseFactory.success(voucherService.createVoucher(request.getData()));
    }

    @PostMapping("/receiveVoucherInfo/{voucherCode}")
    public ResponseEntity<BodyResponse<VoucherResponse>> receiveVoucherInfo(@RequestBody BodyRequest<?> req, @PathVariable String voucherCode) {
        return responseFactory.success(voucherService.getVoucherInfo(voucherCode));
    }

    @PostMapping("/performUpdateVoucherInfo")
    public ResponseEntity<BodyResponse<VoucherResponse>> performUpdateVoucherInfo(@RequestBody BodyRequest<VoucherRequest> req) {
        return responseFactory.success(voucherService.updateVoucherInfo(req.getData()));
    }

    @PostMapping("/receiveVoucherDetailList/{voucherCode}")
    public ResponseEntity<BodyResponse<VoucherDetailResponse>> receiveVoucherDetailList(@RequestBody BodyRequest<?> request
            , @PathVariable String voucherCode, @PageableDefault Pageable pageable) {
        return responseFactory.success(voucherDetailService.getVoucherDetailList(voucherCode, pageable));
    }

    @PostMapping("/receiveVoucherDetailInactiveList/{voucherCode}")
    public ResponseEntity<BodyResponse<VoucherDetailResponse>> receiveVoucherDetailInActiveList(@PathVariable String voucherCode) {
        return responseFactory.success(voucherDetailService.getVoucherDetailReadyForBuyList(voucherCode));
    }

    @PostMapping("/performDeleteVoucher/{voucherCode}")
    public ResponseEntity<BodyResponse<VoucherResponse>> performDeleteVoucher(@RequestBody BodyRequest<?> req, @PathVariable String voucherCode) {
        return  responseFactory.success(voucherService.deleteVoucher(voucherCode));
    }

}
