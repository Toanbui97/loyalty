package vn.com.vpbanks.loyalty.core.service.internal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.vpbanks.loyalty.core.entity.VoucherEntity;
import vn.com.vpbanks.loyalty.core.mapper.VoucherMapper;
import vn.com.vpbanks.loyalty.core.repository.VoucherRepository;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherDetailService;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final VoucherDetailService voucherDetailService;

    @Override
    public List<VoucherResponse> getAllVoucher() {
        List<VoucherEntity> voucherEntityList = voucherRepository.findAll();
        return voucherEntityList.stream().map(voucher -> voucherMapper.entityToDTO(voucher)).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse createVoucher(VoucherRequest request) {
        VoucherEntity entity = voucherMapper.DTOToEntity(request);
        entity = voucherRepository.save(entity);
        voucherDetailService.generateVoucherDetail(entity);
        return voucherMapper.entityToDTO(entity);
    }

}
