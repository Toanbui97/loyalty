package vn.com.loyalty.voucher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.mapper.VoucherDetailMapper;
import vn.com.loyalty.core.service.internal.RedisOperation;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.mapper.VoucherMapper;
import vn.com.loyalty.core.repository.VoucherDetailRepository;
import vn.com.loyalty.core.repository.VoucherRepository;
import vn.com.loyalty.voucher.dto.VoucherMessage;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final VoucherDetailMapper voucherDetailMapper;
    private final VoucherDetailService voucherDetailService;
    private final HttpServletRequest httpRequest;
    private final VoucherDetailRepository voucherDetailRepository;
    private final ObjectMapper mapper;
    private final RedisOperation redisOperation;

    @Override
    public List<VoucherResponse> getAllVoucher() {
        List<VoucherEntity> voucherEntityList = voucherRepository.findAll();
        return voucherEntityList.stream().map(voucherMapper::entityToDTO).toList();
    }

    @Override
    public Page<VoucherResponse> getVoucherList(Pageable page) {
        Page<VoucherEntity> voucherEntityPage = voucherRepository.findAll(page);
        return voucherEntityPage.map(voucherMapper::entityToDTO);
    }

    @Override
    public Page<VoucherResponse> getVoucherListOfCustomer(String customerCode, Pageable page) {
        Page<VoucherDetailEntity> voucherDetailEntityPage = voucherDetailRepository.findByCustomerCode(customerCode, page);
        return voucherDetailEntityPage.map(detail -> {
            VoucherResponse response = voucherMapper.entityToDTO(voucherRepository.findByVoucherCode(detail.getCustomerCode())
                    .orElseThrow(() -> new ResourceNotFoundException(VoucherEntity.class, detail.getVoucherCode())));

            response.setDetailEntities(List.of(voucherDetailMapper.entityToDTO(detail)));

            return response;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse createVoucher(VoucherRequest request) {
        VoucherEntity voucherEntity = voucherMapper.DTOToEntity(request);
        voucherEntity.setVoucherCode(UUID.randomUUID().toString());
        voucherEntity.setInactive(false);
        voucherEntity = voucherRepository.save(voucherEntity);
        redisOperation.setValue(Constants.RedisConstants.VOUCHER_DIR + voucherEntity.getVoucherCode(), voucherEntity);
        return voucherMapper.entityToDTO(voucherEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse processOrchestrationBuyVoucher(VoucherMessage message) {

        VoucherEntity voucherEntity = voucherRepository.findByVoucherCode(message.getVoucherCode()).orElseThrow(
                () -> new ResourceNotFoundException(VoucherEntity.class, message.getVoucherCode()));
        List<VoucherDetailEntity> voucherDetailEntityList = voucherDetailService.generateVoucherDetail(voucherEntity, message.getCustomerCode(), message.getNumberVoucher());

        return voucherMapper.entityToDTO(voucherEntity);
    }




}
