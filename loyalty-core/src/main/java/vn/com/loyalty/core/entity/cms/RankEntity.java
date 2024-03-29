package vn.com.loyalty.core.entity.cms;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.entity.BaseEntity;

import java.io.Serializable;
import java.math.BigDecimal;


@Table(name = "rank", schema = "cms")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class RankEntity extends BaseEntity implements Serializable {

    @Builder.Default
    String rankCode = Constants.MasterDataKey.RANK_DEFAULT;
    @Builder.Default
    String rankName = Constants.MasterDataKey.RANK_DEFAULT;
    @Builder.Default
    BigDecimal requirePoint = BigDecimal.ZERO;
    @Builder.Default
    BigDecimal keepPoint = BigDecimal.ZERO;
}
