package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;

import java.util.List;

public interface VoucherService {
    List<VoucherResponse> getAllVoucher();

    VoucherResponse createVoucher(VoucherRequest request);

    VoucherResponse buyVoucher(String voucherCode);
}
