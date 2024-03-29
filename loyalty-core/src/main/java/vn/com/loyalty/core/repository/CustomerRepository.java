package vn.com.loyalty.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.cms.CustomerEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByCustomerCode(String customerCode);
    Boolean existsByCustomerCode(String customerCode);
    List<CustomerEntity> findByRankExpired(LocalDate expireDay);
    Page<CustomerEntity> findByRankExpired(LocalDate expireDay, Pageable page);

    Optional<CustomerEntity> findFirstByCustomerName(String customerName);
    List<CustomerEntity> findByCustomerCodeIn(List<String> customerCodes);
}
