package vn.com.loyalty.core.entity.transaction;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "rpoint_gain", schema = "transaction")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class RpointGainEntity extends BaseEntity {

    String transactionId;
    String customerCode;
    BigDecimal rpoint;
    LocalDateTime day;

}
