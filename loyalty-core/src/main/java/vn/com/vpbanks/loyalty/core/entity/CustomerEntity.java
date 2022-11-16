package vn.com.vpbanks.loyalty.core.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Entity;
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
    int activeVoucher;

}
