package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.loyalty.core.entity.VoucherEntity;

import java.util.List;

public interface VoucherDetailService {
    List<VoucherDetailResponse> getAllVoucherDetail(String voucherCode);
    List<VoucherDetailResponse> generateVoucherDetail(VoucherEntity voucher);
    List<VoucherDetailResponse> getVoucherDetailReadyForBuyList(String voucherCode);
}
