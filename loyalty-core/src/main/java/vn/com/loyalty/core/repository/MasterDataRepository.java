package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.loyalty.core.entity.MasterDataEntity;

import java.util.Optional;

public interface MasterDataRepository extends JpaRepository<MasterDataEntity, Long> {

    Optional<MasterDataEntity> findByKey(String key);
}
