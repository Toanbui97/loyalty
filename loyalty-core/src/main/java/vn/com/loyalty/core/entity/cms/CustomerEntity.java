package vn.com.loyalty.core.entity.cms;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;

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
    BigDecimal totalEloy;

    @PrePersist
    public void prePersist() {
        if (this.activeVoucher == null) {
            this.activeVoucher = 0L;
        }

        if (this.totalEloy == null) {
            this.totalEloy = BigDecimal.ZERO;
        }

        if (this.totalEpoint == null) {
            this.totalEpoint = BigDecimal.ZERO;
        }
    }

}
