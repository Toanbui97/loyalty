package vn.com.loyalty.core.entity.voucher;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

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
    Long transactionId;
    @Enumerated(EnumType.STRING)
    VoucherStatusCode status;

}
