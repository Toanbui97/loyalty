package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.transaction.RpointGainEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RpointGainRepository extends JpaRepository<RpointGainEntity, Long>, JpaSpecificationExecutor<RpointGainEntity> {
    List<RpointGainEntity> findByCustomerCodeAndDay(String customerCode, LocalDateTime yesterday);
}
