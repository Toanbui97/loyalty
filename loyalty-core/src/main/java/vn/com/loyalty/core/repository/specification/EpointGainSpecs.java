package vn.com.loyalty.core.repository.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import vn.com.loyalty.core.constant.enums.PointStatus;
import vn.com.loyalty.core.entity.cms.EpointGainEntity;
import vn.com.loyalty.core.entity.cms.EpointGainEntity_;

import java.util.ArrayList;
import java.util.List;

public class EpointGainSpecs {

    public static Specification<EpointGainEntity> byCustomerCodeAndStatus(String customerCode, PointStatus status) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            predicateList.add(criteriaBuilder.equal(root.get(EpointGainEntity_.CUSTOMER_CODE), customerCode));
            predicateList.add(criteriaBuilder.equal(root.get(EpointGainEntity_.STATUS), status));

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static Sort orderByExpireDayDESC() {
        return Sort.by(Sort.Direction.DESC, EpointGainEntity_.EXPIRE_DAY);
    }

}
