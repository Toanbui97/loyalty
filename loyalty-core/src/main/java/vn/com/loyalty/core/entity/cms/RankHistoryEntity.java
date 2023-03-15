package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "rank", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class RankHistoryEntity extends BaseEntity {

    String customerCode;
    @Builder.Default
    String rankCode = Constants.MasterDataKey.RANK_DEFAULT;
    @Builder.Default
    BigDecimal rpointGain = BigDecimal.ZERO;
    @Builder.Default
    LocalDate updatedDate = LocalDate.now();


}
