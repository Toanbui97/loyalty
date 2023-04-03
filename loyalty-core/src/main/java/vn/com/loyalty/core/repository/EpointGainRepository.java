package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;

import java.util.Optional;

@Repository
public interface EpointGainRepository extends JpaRepository<EpointGainEntity, Long>, JpaSpecificationExecutor<EpointGainEntity> {

    Optional<EpointGainEntity> findByTransactionId(String transactionId);
}
