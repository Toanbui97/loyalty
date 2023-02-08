package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.cms.RankEntity;

import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<RankEntity, Long>, JpaSpecificationExecutor<RankEntity> {
    boolean existsByRankCode(String rankCode);

    boolean existsByRankName(String rankName);

    Optional<RankEntity> findByRankCode(String rankCode);
}
