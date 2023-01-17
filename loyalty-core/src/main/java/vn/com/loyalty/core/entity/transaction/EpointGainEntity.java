package vn.com.loyalty.core.entity.transaction;

import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.entity.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "gain_epoint", schema = "transaction")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class EpointGainEntity extends BaseEntity {

    private String customerCode;
    private BigDecimal epointGain;
    private LocalDateTime day;

}
