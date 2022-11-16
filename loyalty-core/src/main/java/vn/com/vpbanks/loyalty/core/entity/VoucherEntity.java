package vn.com.vpbanks.loyalty.core.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.Table;

@Table(name = "voucher", schema = "voucher")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherEntity extends BaseEntity {

    String voucherName;
    String voucherCode;
    String description;
    Long totalVoucher;
}
