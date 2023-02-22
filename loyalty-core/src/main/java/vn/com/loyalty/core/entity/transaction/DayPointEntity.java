package vn.com.loyalty.core.entity.transaction;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Table(name = "day_point", schema = "transaction")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DayPointEntity extends BaseEntity {

    String customerCode;
    @Builder.Default
    BigDecimal epointGain = BigDecimal.ZERO;
    @Builder.Default
    BigDecimal rpointGain = BigDecimal.ZERO;
    @Builder.Default
    BigDecimal epointSpend = BigDecimal.ZERO;
    BigDecimal epointExpire;
    LocalDateTime epointExpireTime;
    BigDecimal epointRemain = this.epointGain;
    @Enumerated(EnumType.STRING)
    PointStatus status;
    @Builder.Default
    LocalDateTime transactionDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

    @PrePersist
    public void init() {
        if (this.epointGain == null) {
            this.epointGain = BigDecimal.ZERO;
        }

        if (this.rpointGain == null) {
            this.rpointGain = BigDecimal.ZERO;
        }
        if (this.epointSpend == null) {
            this.epointSpend = BigDecimal.ZERO;
        }
        if (this.transactionDay == null) {
            this.transactionDay = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        }
    }
}
