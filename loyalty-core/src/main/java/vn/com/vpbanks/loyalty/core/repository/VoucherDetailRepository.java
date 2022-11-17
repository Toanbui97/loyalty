package vn.com.vpbanks.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.vpbanks.loyalty.core.constant.enums.StatusCode;
import vn.com.vpbanks.loyalty.core.entity.VoucherDetailEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherDetailRepository extends JpaRepository<VoucherDetailEntity, Long> {
    List<VoucherDetailEntity> findByVoucherCode(String voucherCode);
    Optional<VoucherDetailEntity> findFirstByVoucherCodeAndStatus(String voucherCode, StatusCode status);
    List<VoucherDetailEntity> findByVoucherCodeAndStatus(String voucherCode, StatusCode status);
}
