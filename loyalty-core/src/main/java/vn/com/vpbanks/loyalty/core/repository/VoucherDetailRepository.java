package vn.com.vpbanks.loyalty.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.com.vpbanks.loyalty.core.entity.VoucherDetailEntity;

import java.util.List;

@Repository
public interface VoucherDetailRepository extends JpaRepository<VoucherDetailEntity, Long> {
    List<VoucherDetailEntity> findByVoucherDetailCode(String voucherCode);
}
