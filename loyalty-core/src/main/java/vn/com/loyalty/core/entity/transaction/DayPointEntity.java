package vn.com.loyalty.core.entity.transaction;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.CustomerPointStatus;
import vn.com.loyalty.core.entity.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Table(name = "gain_point", schema = "transaction")
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
    LocalDateTime expireTime;
    BigDecimal remainPoint = this.epointGain;
    @Enumerated(EnumType.STRING)
    CustomerPointStatus status;
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
