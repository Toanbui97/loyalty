package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.loyalty.core.entity.transaction.EpointGainEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface EpointGainRepository extends JpaRepository<EpointGainEntity, Long> {

    List<EpointGainEntity> findByCustomerCodeAndDay(String customerCode, LocalDateTime day);
}
