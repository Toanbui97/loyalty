package vn.com.loyalty.core.service.internal;

import vn.com.loyalty.core.dto.message.TransactionMessageDTO;
import vn.com.loyalty.core.entity.transaction.GainPointEntity;

import java.math.BigDecimal;

public interface GainPointService {
    GainPointEntity saveGainPoint(GainPointEntity gainPointEntity);

}
