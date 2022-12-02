package vn.com.loyalty.core.service.internal.impl.voucher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.mapper.VoucherDetailMapper;
import vn.com.loyalty.core.repository.VoucherDetailRepository;
import vn.com.loyalty.core.service.internal.VoucherDetailService;

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
    public Page<VoucherDetailResponse> getVoucherDetailList(String voucherCode, Pageable pageable) {
        Page<VoucherDetailEntity> voucherDetailEntityPage = voucherDetailRepository.findByVoucherCode(voucherCode, pageable);
        return voucherDetailEntityPage.map(voucherDetailMapper::entityToDTO);
    }

    @Override
    public List<VoucherDetailResponse> generateVoucherDetail(VoucherEntity voucher) {

        List<VoucherDetailEntity> voucherDetailEntityList = new ArrayList<>();
        for (int i = 0; i < voucher.getTotalVoucher(); i++) {
            voucherDetailEntityList.add(VoucherDetailEntity.builder()
                    .voucherCode(voucher.getVoucherCode())
                    .voucherDetailCode(this.generateVoucherDetailCode(voucher.getVoucherName()))
                    .status(VoucherStatusCode.READY_FOR_BUY)
                    .build());
        }

        return voucherDetailRepository.saveAll(voucherDetailEntityList).stream().map(voucherDetailMapper::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public List<VoucherDetailResponse> getVoucherDetailReadyForBuyList(String voucherCode) {
        List<VoucherDetailEntity> voucherDetailInActiveEntityList = voucherDetailRepository.findByVoucherCodeAndStatus(voucherCode, VoucherStatusCode.READY_FOR_BUY);
        return voucherDetailInActiveEntityList.stream().map(voucherDetailMapper::entityToDTO).collect(Collectors.toList());
    }

    private String generateVoucherDetailCode(String voucherName) {
        return voucherName + UUID.randomUUID();
    }
}
