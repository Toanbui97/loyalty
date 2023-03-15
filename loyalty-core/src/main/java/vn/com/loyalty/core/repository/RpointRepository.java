package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.cms.RpointEntity;

@Repository
public interface RpointRepository extends JpaRepository<RpointEntity, Long>, JpaSpecificationExecutor<RpointEntity> {

}
