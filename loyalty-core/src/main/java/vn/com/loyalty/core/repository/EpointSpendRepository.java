package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.loyalty.core.entity.transaction.EpointSpendEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface EpointSpendRepository extends JpaRepository<EpointSpendEntity, Long> {

    List<EpointSpendEntity> findByCustomerCodeAndDay(String customerCode, LocalDateTime day);
}