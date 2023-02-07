package vn.com.loyalty.core.service.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;

import java.util.List;

public interface VoucherService {
    List<VoucherResponse> getAllVoucher();

    Page<VoucherResponse> getVoucherListOfCustomer(String customerCode, Pageable page);

    VoucherResponse createVoucher(VoucherRequest request);

    VoucherResponse buyVoucher(String voucherCode);

    Page<VoucherResponse> getVoucherList(Pageable page);


}
