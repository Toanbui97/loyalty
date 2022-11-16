package vn.com.vpbanks.loyalty.core.service.internal;

import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherResponse;

import java.util.List;

public interface VoucherService {
    List<VoucherResponse> getAllVoucher();

    VoucherResponse createVoucher(VoucherRequest request);
}
