package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.transaction.CustomerPointEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerPointRepository extends JpaRepository<CustomerPointEntity, Long>, JpaSpecificationExecutor<CustomerPointEntity> {

    Optional<CustomerPointEntity> findById(Long customerPointId);
    List<CustomerPointEntity> findByTransactionDay(LocalDateTime day);
    List<CustomerPointEntity> findAll(Specification specs);
}
