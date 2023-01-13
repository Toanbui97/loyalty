package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.transaction.GainPointEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GainPointRepository extends JpaRepository<GainPointEntity, Long> {

    List<GainPointEntity> findByCustomerCodeAndExpireTimeGreaterThanEqual(String customerCode, LocalDateTime time);

}
