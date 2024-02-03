package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.constant.enums.*;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;

import java.time.*;
import java.util.*;

@Repository
public interface EpointGainRepository extends JpaRepository<EpointGainEntity, Long>, JpaSpecificationExecutor<EpointGainEntity> {

    Optional<EpointGainEntity> findByTransactionId(String transactionId);

    List<EpointGainEntity> findByStatus(PointStatus status);

    List<EpointGainEntity> findByExpireDayBefore(LocalDate localDate);
}
