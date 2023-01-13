package vn.com.loyalty.core.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;

import java.util.List;

public interface VoucherDetailService {
    Page<VoucherDetailResponse> getVoucherDetailList(String voucherCode, Pageable pageable);
    Page<VoucherDetailResponse> getVoucherDetailListOfCustomer(String customerCode, Pageable pageable);
    List<VoucherDetailResponse> generateVoucherDetail(VoucherEntity voucher);
    List<VoucherDetailResponse> getVoucherDetailReadyForBuyList(String voucherCode);
}
