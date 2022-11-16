package vn.com.vpbanks.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.vpbanks.loyalty.core.entity.VoucherEntity;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {

    Optional<VoucherEntity> findByVoucherCode(String voucherCode);
}
