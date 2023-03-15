package vn.com.loyalty.core.repository.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import vn.com.loyalty.core.entity.cms.RpointEntity;
import vn.com.loyalty.core.entity.cms.RpointEntity_;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RpointGainSpecs {

    public static Specification<RpointEntity> fromLastUpdate(LocalDate transactionDay) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();

            predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.<LocalDate>get(RpointEntity_.TRANSACTION_DAY), transactionDay));

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
