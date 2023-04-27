package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

@Table(name = "customer", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class CustomerEntity extends BaseEntity {

    @Builder.Default
    String customerName = "Customer name " + UUID.randomUUID();
    @Builder.Default
    String customerCode = UUID.randomUUID().toString();
    @Builder.Default
    Long activeVoucher = 0L;
    @Builder.Default
    BigDecimal epoint = BigDecimal.ZERO;
    @Builder.Default
    BigDecimal rpoint = BigDecimal.ZERO;
    @Builder.Default
    String rankCode = Constants.MasterDataKey.RANK_DEFAULT;
    @Builder.Default
    LocalDate rankExpired = LocalDate.now();
    @Builder.Default
    LocalDate lastUpdatedRank = LocalDate.now();

}
