package vn.com.loyalty.core.entity.voucher;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    boolean inactive;
    Long totalVoucher;
    BigDecimal discountPercent;
    LocalDate expireTime;
    BigDecimal price;
}
