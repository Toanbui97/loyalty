package vn.com.loyalty.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherDetailRepository extends JpaRepository<VoucherDetailEntity, Long> {
    Page<VoucherDetailEntity> findByVoucherCode(String voucherCode, Pageable pageable);
    Optional<VoucherDetailEntity> findFirstByVoucherCodeAndStatus(String voucherCode, VoucherStatusCode status);
    List<VoucherDetailEntity> findByVoucherCodeAndStatus(String voucherCode, VoucherStatusCode status);
}
