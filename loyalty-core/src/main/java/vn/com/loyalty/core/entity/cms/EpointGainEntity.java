package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    BigDecimal epoint;
    BigDecimal epointRemain;
    LocalDate transactionDay;
    @Enumerated(EnumType.STRING)
    PointStatus status;
    LocalDate expireDay;
    String source;

}
