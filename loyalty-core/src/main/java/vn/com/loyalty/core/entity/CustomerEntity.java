package vn.com.loyalty.core.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;

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

    @PrePersist
    public void prePersist() {
        if (this.activeVoucher == null) {
            this.activeVoucher = 0L;
        }
    }

}