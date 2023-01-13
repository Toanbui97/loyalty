package vn.com.loyalty.core.dto.response.voucher;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VoucherResponse {

    String voucherName;
    String voucherCode;
    String description;
    Long totalVoucher;
    Long active;
    Long inActive;
    List<VoucherDetailResponse> detailEntities;
}
