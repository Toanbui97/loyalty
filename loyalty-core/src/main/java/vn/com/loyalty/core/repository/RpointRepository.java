package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.cms.RpointEntity;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RpointRepository extends JpaRepository<RpointEntity, Long>, JpaSpecificationExecutor<RpointEntity> {
    List<RpointEntity> findByCustomerCodeAndDay(String customerCode, LocalDateTime yesterday);
}
