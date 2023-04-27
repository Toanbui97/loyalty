package vn.com.loyalty.voucher.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.voucher.dto.VoucherOrchestrationMessage;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherDetailService {
    Page<VoucherDetailResponse> getVoucherDetailList(String voucherCode, Pageable pageable);
    Page<VoucherDetailResponse> getVoucherDetailListOfCustomer(String customerCode, Pageable pageable);
    List<VoucherDetailEntity> generateVoucherDetail(VoucherEntity voucher, VoucherOrchestrationMessage message);

    List<VoucherDetailEntity> generateVoucherDetailFree(List<VoucherEntity> voucherFreeList, String customerCode, String transactionId);

    List<VoucherDetailResponse> getVoucherDetailReadyForBuyList(String voucherCode);
}
