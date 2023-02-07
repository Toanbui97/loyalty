package vn.com.loyalty.core.repository.specification;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;
import vn.com.loyalty.core.entity.transaction.TransactionEntity_;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionSpecs {

    public static Specification<TransactionEntity> inYesterday() {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
            return criteriaBuilder.and(criteriaBuilder.between(root.get(TransactionEntity_.transactionTime), today, today.minusDays(1L)));
        };
    }
}
