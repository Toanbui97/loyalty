package vn.com.loyalty.voucher.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.dto.message.OrchestrationMessage;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationMessage;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity_;
import vn.com.loyalty.core.repository.VoucherDetailRepository;
import vn.com.loyalty.core.repository.specification.VoucherDetailSpecs;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrchestrationServiceImpl implements OrchestrationService {

    private final VoucherDetailRepository voucherDetailRepository;

    @Override
    @Transactional
    public OrchestrationMessage processOrchestrationTransaction(TransactionOrchestrationMessage req) {
        if (CollectionUtils.isEmpty(req.getVoucherDetailCodeList())) return req;

        List<VoucherDetailEntity> voucherDetailEntityList = voucherDetailRepository.findByVoucherDetailCodeIn(req.getVoucherDetailCodeList());

        if (voucherDetailEntityList.size() != req.getVoucherDetailCodeList().size()) {
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
}
