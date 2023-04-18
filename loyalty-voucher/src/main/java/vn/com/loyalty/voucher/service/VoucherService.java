package vn.com.loyalty.voucher.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.voucher.dto.VoucherOrchestrationMessage;

import java.util.List;

public interface VoucherService {
    List<VoucherResponse> getAllVoucher();
    Page<VoucherResponse> getVoucherListOfCustomer(String customerCode, Pageable page);
    VoucherResponse createVoucher(VoucherRequest request);
    Page<VoucherResponse> getVoucherList(Pageable page);
    VoucherResponse processOrchestrationBuyVoucher(VoucherOrchestrationMessage message);

    VoucherResponse rollbackOrchestrationBuyVoucher(VoucherOrchestrationMessage data);

    VoucherResponse getVoucherInfo(String voucherCode);

    VoucherResponse updateVoucherInfo(VoucherRequest request);

    VoucherResponse deleteVoucher(String voucherCode);
}
