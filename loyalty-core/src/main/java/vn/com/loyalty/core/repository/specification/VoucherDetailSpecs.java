package vn.com.loyalty.core.repository.specification;

import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity_;
import vn.com.loyalty.core.repository.VoucherDetailRepository;

import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VoucherDetailSpecs {

    public static Specification<VoucherDetailEntity> useInTransaction(String customerCode, List<String> voucherCodeList) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

//            for (String voucherCode : voucherCodeList) {
//                criteriaBuilder.or(criteriaBuilder.equal(root.get(VoucherDetailEntity_.CUSTOMER_CODE), customerCode),
//                        criteriaBuilder.equal(root.get(VoucherDetailEntity_.VOUCHER_CODE), voucherCode)),
//                        criteriaBuilder.equal(root.get(VoucherDetailEntity_.STATUS), VoucherStatusCode.READY_FOR_BUY);))
//                List<Predicate> subPredicate = new ArrayList<>();
//                subPredicate.add(criteriaBuilder.equal(root.get(VoucherDetailEntity_.CUSTOMER_CODE), customerCode));
//                subPredicate.add(criteriaBuilder.equal(root.get(VoucherDetailEntity_.VOUCHER_CODE), voucherCode));
//                subPredicate.add(criteriaBuilder.equal(root.get(VoucherDetailEntity_.STATUS), VoucherStatusCode.READY_FOR_BUY));
//                predicateList.add(criteriaBuilder.or(subPredicate.toArray(new Predicate[0])));
//            }

            predicateList.add(root.get(VoucherDetailEntity_.VOUCHER_CODE).in(voucherCodeList));
            predicateList.add(criteriaBuilder.equal(root.get(VoucherDetailEntity_.CUSTOMER_CODE), customerCode));
            predicateList.add(criteriaBuilder.equal(root.get(VoucherDetailEntity_.STATUS), VoucherStatusCode.READY_FOR_USE));

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
