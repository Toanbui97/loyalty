package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.transaction.TransactionIncomeEntity;

@Repository
public interface TransactionIncomeRepository extends JpaRepository<TransactionIncomeEntity, Long> {
}
