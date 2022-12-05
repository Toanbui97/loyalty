package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.transaction.EPointEntity;

@Repository
public interface EPointRepository extends JpaRepository<EPointEntity, Long> {
}
