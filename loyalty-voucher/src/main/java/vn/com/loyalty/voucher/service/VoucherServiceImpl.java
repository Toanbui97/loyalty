package vn.com.loyalty.voucher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import vn.com.loyalty.core.utils.ObjectUtil;
import vn.com.loyalty.voucher.dto.VoucherOrchestrationMessage;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<VoucherEntity> voucherEntities = voucherRepository.findByVoucherCodeIn(voucherDetailEntityPage.getContent().stream().map(VoucherDetailEntity::getVoucherCode).distinct().toList());
        List<VoucherResponse> responseList = voucherEntities.stream().map(voucher -> {
            VoucherResponse response = voucherMapper.entityToDTO(voucher);
            response.setDetailEntities(voucherDetailEntityPage.getContent().stream()
                    .filter(detail -> detail.getVoucherCode().equals(voucher.getVoucherCode()))
                    .map(voucherDetailMapper::entityToDTO)
                    .toList());
            return response;
        }).toList();

        return new PageImpl<>(responseList, voucherDetailEntityPage.getPageable(), voucherDetailEntityPage.getTotalElements());
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
    public VoucherResponse processOrchestrationBuyVoucher(VoucherOrchestrationMessage message) {

        VoucherEntity voucherEntity = voucherRepository.findByVoucherCode(message.getVoucherCode()).orElseThrow(
                () -> new ResourceNotFoundException(VoucherEntity.class, message.getVoucherCode()));
        voucherDetailService.generateVoucherDetail(voucherEntity, message);

        return voucherMapper.entityToDTO(voucherEntity);
    }

    @Override
    @Transactional
    public VoucherResponse rollbackOrchestrationBuyVoucher(VoucherOrchestrationMessage data) {
        List<VoucherDetailEntity> voucherDetailEntityList = voucherDetailRepository.findByTransactionId(data.getTransactionId());
        voucherDetailRepository.deleteAll(voucherDetailEntityList);
        return VoucherResponse.builder().build();
    }

    @Override
    public VoucherResponse getVoucherInfo(String voucherCode) {
        return voucherMapper.entityToDTO(voucherRepository.findByVoucherCode(voucherCode)
                .orElseThrow(() -> new ResourceNotFoundException(VoucherDetailEntity.class, voucherCode)));
    }

    @Override
    public VoucherResponse updateVoucherInfo(VoucherRequest request) {
        VoucherEntity voucherEntity = voucherRepository.findByVoucherCode(request.getVoucherCode())
                .orElseThrow(() -> new ResourceNotFoundException(VoucherDetailEntity.class, request.getVoucherCode()));

        voucherEntity = ObjectUtil.mergeObject(request, voucherEntity);
        voucherRepository.save(voucherEntity);
        return voucherMapper.entityToDTO(voucherEntity);
    }

    @Override
    @Transactional
    public VoucherResponse deleteVoucher(String voucherCode) {

        VoucherEntity voucherEntity = voucherRepository.findByVoucherCode(voucherCode).orElseThrow(() -> new ResourceNotFoundException(VoucherEntity.class, voucherCode));
        List<VoucherDetailEntity> voucherDetailEntityList = voucherDetailRepository.findByVoucherCode(voucherCode);

        redisOperation.delete(Constants.RedisConstants.VOUCHER_DIR + voucherCode);
        voucherRepository.delete(voucherEntity);
        voucherDetailRepository.deleteAll(voucherDetailEntityList);

        VoucherResponse voucherResponse = voucherMapper.entityToDTO(voucherEntity);
        voucherResponse.setDetailEntities(voucherDetailEntityList.stream().map(voucherDetailMapper::entityToDTO).toList());

        return voucherResponse;
    }


}
