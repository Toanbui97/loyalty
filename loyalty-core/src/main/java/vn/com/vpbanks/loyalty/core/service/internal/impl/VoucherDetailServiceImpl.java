package vn.com.vpbanks.loyalty.core.service.internal.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.com.vpbanks.loyalty.core.constant.enums.StatusCode;
import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.vpbanks.loyalty.core.entity.VoucherDetailEntity;
import vn.com.vpbanks.loyalty.core.entity.VoucherEntity;
import vn.com.vpbanks.loyalty.core.mapper.VoucherDetailMapper;
import vn.com.vpbanks.loyalty.core.mapper.VoucherMapper;
import vn.com.vpbanks.loyalty.core.repository.VoucherDetailRepository;
import vn.com.vpbanks.loyalty.core.repository.VoucherRepository;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherDetailService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherDetailServiceImpl implements VoucherDetailService {

    private final VoucherDetailRepository voucherDetailRepository;
    private final VoucherDetailMapper voucherDetailMapper;

    @Override
    public List<VoucherDetailResponse> getAllVoucherDetail(String voucherCode) {
        List<VoucherDetailEntity> voucherDetailEntityList = voucherDetailRepository.findByVoucherDetailCode(voucherCode);
        return voucherDetailEntityList.stream().map(v -> voucherDetailMapper.entityToDTO(v)).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDetailResponse> generateVoucherDetail(VoucherEntity voucher) {

        List<VoucherDetailEntity> voucherDetailEntityList = new ArrayList<>();
        for (int i = 0; i < voucher.getTotalVoucher(); i++) {
            voucherDetailEntityList.add(VoucherDetailEntity.builder()
                    .voucherCode(voucher.getVoucherCode())
                    .voucherDetailCode(this.generateVoucherDetailCode(voucher.getVoucherName()))
                    .status(StatusCode.ACTIVE)
                    .build());
        }

        return voucherDetailRepository.saveAll(voucherDetailEntityList).stream().map(v ->
                voucherDetailMapper.entityToDTO(v)).collect(Collectors.toList());
    }

    private String generateVoucherDetailCode(String voucherName) {
        return voucherName + UUID.randomUUID();
    }
}
