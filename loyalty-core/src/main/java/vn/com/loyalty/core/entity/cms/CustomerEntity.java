package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Table(name = "customer", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CustomerEntity extends BaseEntity {

    String customerName;
    String customerCode;
    Long activeVoucher;
    BigDecimal epoint;
    BigDecimal rpoint;
    String rankCode;
    LocalDateTime rankExpired;


    @PrePersist
    public void prePersist() {
        if (this.activeVoucher == null) {
            this.activeVoucher = 0L;
        }

        if (this.rpoint == null) {
            this.rpoint = BigDecimal.ZERO;
        }

        if (this.epoint == null) {
            this.epoint = BigDecimal.ZERO;
        }
        if (!StringUtils.hasText(rankCode)) {
            this.rankCode = "NONE";
        }
        if (this.rankExpired == null) {
            this.rankExpired = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        }

    }

}
