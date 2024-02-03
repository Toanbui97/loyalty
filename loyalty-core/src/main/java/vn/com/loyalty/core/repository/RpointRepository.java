package vn.com.loyalty.core.repository;

import org.springframework.cglib.core.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.cms.RpointEntity;

import java.time.*;
import java.util.*;

@Repository
public interface RpointRepository extends JpaRepository<RpointEntity, Long>, JpaSpecificationExecutor<RpointEntity> {

    Optional<RpointEntity> findByTransactionId(String transactionId);
    List<RpointEntity> findByTransactionDayBefore(LocalDate date);
}
