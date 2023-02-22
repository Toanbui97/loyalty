package vn.com.loyalty.core.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.transaction.EpointGainEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface EpointGainRepository extends JpaRepository<EpointGainEntity, Long>, JpaSpecificationExecutor<EpointGainEntity> {

    List<EpointGainEntity> findByCustomerCodeAndDay(String customerCode, LocalDateTime day);
    List<EpointGainEntity> findByStatus(PointStatus status);

}
