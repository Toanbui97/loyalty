package vn.com.loyalty.core.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.cms.RankHistoryEntity;

import java.util.Optional;

@Repository
public interface RankHistoryRepository extends JpaRepository<RankHistoryEntity, Long>, JpaSpecificationExecutor<RankHistoryEntity> {
    Optional<RankHistoryEntity> findFirstBy(Specification<RankHistoryEntity> specs, Sort sort);
}
