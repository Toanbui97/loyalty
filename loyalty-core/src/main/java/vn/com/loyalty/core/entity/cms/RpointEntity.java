package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "rpoint_gain", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class RpointEntity extends BaseEntity {

    String transactionId;
    String customerCode;
    @Builder.Default
    BigDecimal rpoint = BigDecimal.ZERO;
    @Builder.Default
    LocalDate transactionDay = LocalDate.now();
    @Builder.Default
    String source = "NONE";
    @Builder.Default
    @Enumerated(EnumType.STRING)
    PointStatus status = PointStatus.ACTIVE;

}
