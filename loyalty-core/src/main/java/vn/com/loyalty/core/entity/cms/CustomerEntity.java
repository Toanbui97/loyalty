package vn.com.loyalty.core.entity.cms;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
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
    BigDecimal totalEpoint;
    BigDecimal totalRpoint;
    Long rank;
    LocalDateTime rankExpired;


    @PrePersist
    public void prePersist() {
        if (this.activeVoucher == null) {
            this.activeVoucher = 0L;
        }

        if (this.totalRpoint == null) {
            this.totalRpoint = BigDecimal.ZERO;
        }

        if (this.totalEpoint == null) {
            this.totalEpoint = BigDecimal.ZERO;
        }
        if (this.rank == null) {
            this.rank = 0L;
        }
        if (this.rankExpired == null) {
            this.rankExpired = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        }

    }

}
