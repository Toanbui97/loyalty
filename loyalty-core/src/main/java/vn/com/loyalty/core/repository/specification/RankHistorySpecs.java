package vn.com.loyalty.core.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import vn.com.loyalty.core.entity.cms.EpointGainEntity_;
import vn.com.loyalty.core.entity.cms.RankHistoryEntity;
import vn.com.loyalty.core.entity.cms.RankHistoryEntity_;

import java.util.ArrayList;
import java.util.List;

public class RankHistorySpecs {

    public static Specification<RankHistoryEntity> lastUpdated(String customerCode) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(criteriaBuilder.equal(root.get(RankHistoryEntity_.CUSTOMER_CODE), customerCode));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static Sort orderByUpdatedDESC() {
        return Sort.by(Sort.Direction.DESC, RankHistoryEntity_.UPDATE_AT);
    }

}
