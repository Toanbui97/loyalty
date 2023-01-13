package vn.com.loyalty.core.entity.voucher;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

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
    Long inactiveVoucher;
    Long totalVoucher;
    BigDecimal discountPercent;
}
