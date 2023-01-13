package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.transaction.GainPointEntity;
import vn.com.loyalty.core.repository.GainPointRepository;
import vn.com.loyalty.core.service.internal.GainPointService;

@Service
@RequiredArgsConstructor
public class GainPointServiceImpl implements GainPointService {

    private final GainPointRepository gainPointRepository;

    @Override
    public GainPointEntity saveGainPoint(GainPointEntity gainPointEntity) {
        return gainPointRepository.save(gainPointEntity);
    }

}
