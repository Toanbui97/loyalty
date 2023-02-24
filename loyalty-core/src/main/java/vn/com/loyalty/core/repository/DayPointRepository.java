package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.constant.enums.PointStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DayPointRepository extends JpaRepository<DayPointEntity, Long>, JpaSpecificationExecutor<DayPointEntity> {

    Optional<DayPointEntity> findById(Long customerPointId);
    List<DayPointEntity> findByTransactionDay(LocalDateTime day);
    List<DayPointEntity> findAll(Specification specs);
    List<DayPointEntity> findByStatus(PointStatus status);
    List<DayPointEntity> findByEpointExpireTime(LocalDateTime expireDay);
}
