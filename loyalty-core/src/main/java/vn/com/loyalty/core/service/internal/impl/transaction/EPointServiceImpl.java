package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.transaction.EPointEntity;
import vn.com.loyalty.core.repository.EPointRepository;
import vn.com.loyalty.core.service.internal.impl.EPointService;

@Service
@RequiredArgsConstructor
public class EPointServiceImpl implements EPointService {

    private final EPointRepository ePointRepository;
    @Override
    public EPointEntity saveEpoint(EPointEntity ePointEntity) {
        return ePointRepository.save(ePointEntity);
    }
}
