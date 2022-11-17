package vn.com.vpbanks.loyalty.core.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.vpbanks.loyalty.core.constant.enums.VoucherStatusCode;

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
    boolean inactive;
    @Enumerated(EnumType.STRING)
    VoucherStatusCode status;
}
