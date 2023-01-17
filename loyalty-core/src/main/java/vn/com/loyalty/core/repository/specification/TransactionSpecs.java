package vn.com.loyalty.core.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity_;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TransactionSpecs {

    public static Specification<TransactionEntity> findByDay() {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            return criteriaBuilder.and(criteriaBuilder.between(root.get(TransactionEntity_.transactionTime), today, today.minusDays(1L)));
        };
    }
}
