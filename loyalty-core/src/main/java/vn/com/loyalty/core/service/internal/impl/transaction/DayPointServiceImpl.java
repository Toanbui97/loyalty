package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.transaction.DayPointEntity;
import vn.com.loyalty.core.repository.DayPointRepository;
import vn.com.loyalty.core.service.internal.DayPointService;

@Service
@RequiredArgsConstructor
public class DayPointServiceImpl implements DayPointService {

    private final DayPointRepository dayPointRepository;

    @Override
    public DayPointEntity saveGainPoint(DayPointEntity dayPointEntity) {
        return dayPointRepository.save(dayPointEntity);
    }

}
