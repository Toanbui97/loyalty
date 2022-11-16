package vn.com.vpbanks.loyalty.core.service.internal;

import org.springframework.boot.configurationprocessor.json.JSONException;
import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.vpbanks.loyalty.core.exception.ResourceNotFoundException;

import java.util.List;

public interface VoucherService {
    List<VoucherResponse> getAllVoucher();

    VoucherResponse createVoucher(VoucherRequest request);

    VoucherResponse activeVoucher(String voucherCode);
}
