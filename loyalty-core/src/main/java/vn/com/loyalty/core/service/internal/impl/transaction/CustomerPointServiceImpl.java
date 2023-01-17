package vn.com.loyalty.core.service.internal.impl.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.entity.transaction.CustomerPointEntity;
import vn.com.loyalty.core.repository.CustomerPointRepository;
import vn.com.loyalty.core.service.internal.CustomerPointService;

@Service
@RequiredArgsConstructor
public class CustomerPointServiceImpl implements CustomerPointService {

    private final CustomerPointRepository customerPointRepository;

    @Override
    public CustomerPointEntity saveGainPoint(CustomerPointEntity customerPointEntity) {
        return customerPointRepository.save(customerPointEntity);
    }

}
