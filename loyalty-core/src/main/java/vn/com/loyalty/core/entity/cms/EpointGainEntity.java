package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "epoint_gain", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class EpointGainEntity extends BaseEntity {

    String customerCode;
    String transactionId;
    @Builder.Default
    BigDecimal epoint = BigDecimal.ZERO;
    BigDecimal epointRemain;
    @Builder.Default
    LocalDate transactionDay = LocalDate.now();
    @Enumerated(EnumType.STRING)
    @Builder.Default
    PointStatus status = PointStatus.ACTIVE;
    LocalDate expireDay;
    @Builder.Default
    String source = "NONE";

    @PrePersist
    public void prePersist () {
        if (this.epointRemain == null) {
            this.epointRemain = this.epoint;
        }
    }

}
