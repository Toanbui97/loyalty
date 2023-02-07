package vn.com.loyalty.core.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import vn.com.loyalty.core.entity.MasterDataEntity;

public interface MasterDataRepository extends JpaRepository<MasterDataEntity, Long> {

    @Cacheable
    MasterDataEntity findByKey(String key);
}
