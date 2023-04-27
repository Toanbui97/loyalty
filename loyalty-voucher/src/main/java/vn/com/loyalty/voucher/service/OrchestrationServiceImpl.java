package vn.com.loyalty.voucher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;
import vn.com.loyalty.core.entity.cms.CustomerEntity;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.repository.VoucherDetailRepository;
import vn.com.loyalty.core.repository.VoucherRepository;
import vn.com.loyalty.core.repository.specification.VoucherDetailSpecs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

    private final VoucherDetailRepository voucherDetailRepository;
    private final VoucherRepository voucherRepository;
    private final VoucherDetailService voucherDetailService;

    @Override
    @Transactional
    public OrchestrationMessage processOrchestrationTransaction(TransactionOrchestrationMessage req) {
        if (CollectionUtils.isEmpty(req.getVoucherCodeList())) return req;

        List<VoucherEntity> voucherFreeList = voucherRepository.findByVoucherCodeInAndPrice(req.getVoucherCodeList(), BigDecimal.ZERO);
        if (!CollectionUtils.isEmpty(voucherFreeList)) voucherDetailService.generateVoucherDetailFree(voucherFreeList, req.getCustomerCode(), req.getTransactionId());

        List<VoucherDetailEntity> voucherDetailEntityList = voucherDetailRepository.findAll(VoucherDetailSpecs.useInTransaction(req.getCustomerCode(), req.getVoucherCodeList()));
        voucherDetailEntityList = voucherDetailEntityList.stream().filter(distinctByVoucherCode(VoucherDetailEntity::getVoucherCode))
                .toList();

        if (voucherDetailEntityList.size() != req.getVoucherCodeList().size()) {
            throw new RuntimeException();
        }

        voucherDetailEntityList = voucherDetailEntityList.stream().map(voucherDetail -> {
            voucherDetail.setStatus(VoucherStatusCode.USED);
            voucherDetail.setTransactionId(req.getTransactionId());
            return voucherDetail;
        }).toList();

        voucherDetailRepository.saveAll(voucherDetailEntityList);

        return req;
    }

    @Override
    public OrchestrationMessage rollbackOrchestrationTransaction(OrchestrationMessage req) {

        List<VoucherDetailEntity> voucherDetailEntityList = voucherDetailRepository.findByTransactionId(req.getTransactionId());
        voucherDetailRepository.saveAll(voucherDetailEntityList.stream().map(voucher -> {
            voucher.setStatus(VoucherStatusCode.READY_FOR_USE);
            return voucher;
        }).toList());
        return req;
    }

    private Predicate<VoucherDetailEntity> distinctByVoucherCode(Function<VoucherDetailEntity, String> extractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(extractor.apply(t), Boolean.TRUE) == null;
    }
}
