package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.transaction.TransactionMessageEntity;

@Repository
public interface TransactionMessageRepository extends JpaRepository<TransactionMessageEntity, Long> {
}
