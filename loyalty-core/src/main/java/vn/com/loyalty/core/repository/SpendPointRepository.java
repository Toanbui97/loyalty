package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.transaction.SpendPointEntity;

import java.util.List;

@Repository
public interface SpendPointRepository extends JpaRepository<SpendPointEntity, Long> {

    List<SpendPointEntity> findByCustomerCode(String customerCode);
}
