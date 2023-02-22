package vn.com.loyalty.core.entity.voucher;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.entity.BaseEntity;

@Table(name = "voucher_detail", schema = "voucher")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherDetailEntity extends BaseEntity {

    String voucherCode;
    String voucherDetailCode;
    String customerCode;
    String transactionId;
    @Enumerated(EnumType.STRING)
    VoucherStatusCode status;

}
