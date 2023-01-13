package vn.com.loyalty.core.entity.cache;


import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString
@Builder
public class CustomerPointCache implements Serializable {

    private BigDecimal epoint;
    private BigDecimal rpoint;

}
