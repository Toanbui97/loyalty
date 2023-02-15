package vn.com.loyalty.core.repository.specification;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import vn.com.loyalty.core.constant.enums.CustomerPointStatus;
import vn.com.loyalty.core.entity.transaction.DayPointEntity;
import vn.com.loyalty.core.entity.transaction.DayPointEntity_;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CustomerPointSpecs {

    public static Specification<DayPointEntity> findByDay(String customerCode, LocalDateTime day) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            if (StringUtils.hasText(customerCode)) {
                predicateList.add(criteriaBuilder.equal(root.get(DayPointEntity_.CUSTOMER_CODE), customerCode));
            }
            LocalDateTime timeTruncate = day != null ? day.truncatedTo(ChronoUnit.DAYS) : LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            predicateList.add(criteriaBuilder.equal(root.get(DayPointEntity_.TRANSACTION_DAY), timeTruncate));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static Specification<DayPointEntity> findFromDay(String customerCode, LocalDateTime day) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.hasText(customerCode)) {
                predicateList.add(criteriaBuilder.equal(root.get(DayPointEntity_.CUSTOMER_CODE), customerCode));
            }
            LocalDateTime timeTruncate = day != null ? day.truncatedTo(ChronoUnit.DAYS) : LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get(DayPointEntity_.TRANSACTION_DAY), timeTruncate));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static Specification<DayPointEntity> byCustomerCodeAndActive(String customerCode) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.hasText(customerCode)) {
                predicateList.add(criteriaBuilder.equal(root.get(DayPointEntity_.CUSTOMER_CODE), customerCode));
            }
            predicateList.add(criteriaBuilder.equal(root.get(DayPointEntity_.STATUS), CustomerPointStatus.ACTIVE));
            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }

    public static Sort orderByDayDESC() {
        return Sort.by(Sort.Direction.DESC, DayPointEntity_.TRANSACTION_DAY);
    }

}
