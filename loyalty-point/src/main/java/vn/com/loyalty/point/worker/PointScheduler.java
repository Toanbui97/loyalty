package vn.com.loyalty.point.worker;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.entity.transaction.GainPointEntity;
import vn.com.loyalty.core.repository.GainPointRepository;
import vn.com.loyalty.core.repository.MasterDataRepository;
import vn.com.loyalty.core.repository.SpendPointRepository;
import vn.com.loyalty.core.service.internal.RedisOperation;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PointScheduler {

    private final GainPointRepository gainPointRepository;
    private final MasterDataRepository masterDataRepository;
    private final RedisOperation redisOperation;
    private final SpendPointRepository spendPointRepository;

    @Transactional
    public void epointSchedule() {

        CustomerEntity customerEntity = new CustomerEntity();

        List<GainPointEntity> gainPointList = gainPointRepository
                .findByCustomerCodeAndExpireTimeGreaterThanEqual(customerEntity.getCustomerCode(), (LocalDateTime.now().truncatedTo(ChronoUnit.DAYS)));

//        List<SpendPointEntity> spendPointList = spendPointRepository
//                .findByCustomerCode()

        BigDecimal totalGainPoint = gainPointList.stream().map(GainPointEntity::getEpointGain)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

//        BigDecimal spendPoint =






    }


    public void sumRpoint() {

    }




}
