package vn.com.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {

    Optional<VoucherEntity> findByVoucherCode(String voucherCode);
    List<VoucherEntity> findByVoucherCodeIn(List<String> voucherCode);
    List<VoucherEntity> findByVoucherCodeInAndPrice(List<String> voucherCode, BigDecimal price);
    List<VoucherEntity> findByPrice(BigDecimal price);
}
