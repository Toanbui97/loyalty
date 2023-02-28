package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.loyalty.core.entity.cms.EpointSpendEntity;

import java.time.LocalDate;
import java.util.List;

public interface EpointSpendRepository extends JpaRepository<EpointSpendEntity, Long> {

    List<EpointSpendEntity> findByCustomerCodeAndTransactionDay(String customerCode, LocalDate day);
}
