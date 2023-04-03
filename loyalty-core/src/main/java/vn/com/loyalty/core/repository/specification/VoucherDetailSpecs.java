package vn.com.loyalty.core.repository.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity_;
import vn.com.loyalty.core.repository.VoucherDetailRepository;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoucherDetailSpecs {

    public static Specification<VoucherDetailEntity> useInTransaction(String customerCode, List<String> voucherDetailCodeList) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicateList = new ArrayList<>();

            predicateList.add(criteriaBuilder.equal(root.get(VoucherDetailEntity_.CUSTOMER_CODE), customerCode));
            predicateList.add(root.get(VoucherDetailEntity_.VOUCHER_DETAIL_CODE).in(voucherDetailCodeList));

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
