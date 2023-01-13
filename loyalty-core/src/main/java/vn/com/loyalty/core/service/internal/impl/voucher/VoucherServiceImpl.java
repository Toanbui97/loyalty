package vn.com.loyalty.core.service.internal.impl.voucher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vn.com.loyalty.core.exception.ResourceNotFoundException;
import vn.com.loyalty.core.utils.RequestUtil;
import vn.com.loyalty.core.constant.Constants;
import vn.com.loyalty.core.constant.enums.VoucherStatusCode;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;
import vn.com.loyalty.core.mapper.VoucherMapper;
import vn.com.loyalty.core.repository.VoucherDetailRepository;
import vn.com.loyalty.core.repository.VoucherRepository;
import vn.com.loyalty.core.service.internal.VoucherDetailService;
import vn.com.loyalty.core.service.internal.VoucherService;
import vn.com.loyalty.core.thirdparty.service.CmsWebClient;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;
    private final VoucherDetailService voucherDetailService;
    private final HttpServletRequest httpRequest;
    private final VoucherDetailRepository voucherDetailRepository;
    private final CmsWebClient cmsWebClient;
    private final ObjectMapper mapper;

    @Override
    public List<VoucherResponse> getAllVoucher() {
        List<VoucherEntity> voucherEntityList = voucherRepository.findAll();
        return voucherEntityList.stream().map(voucherMapper::entityToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<VoucherResponse> getVoucherList(Pageable page) {
        Page<VoucherEntity> voucherEntityPage = voucherRepository.findAll(page);
        return voucherEntityPage.map(voucherMapper::entityToDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse createVoucher(VoucherRequest request) {
        VoucherEntity entity = voucherMapper.DTOToEntity(request);
        entity.setVoucherCode(UUID.randomUUID().toString());
        entity.setInactiveVoucher(entity.getTotalVoucher());
        entity = voucherRepository.save(entity);
        voucherDetailService.generateVoucherDetail(entity);
        return voucherMapper.entityToDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse buyVoucher(String voucherCode) {

        String customerCode = RequestUtil.extractCustomerCodeFromToken(httpRequest.getHeader(Constants.AUTH_HEADER));
        CustomerResponse customerResponse = mapper.convertValue(cmsWebClient.receiveCustomerInfo(
                BodyRequest.of(CustomerRequest.builder().customerCode(customerCode).build()))
                .getData(), CustomerResponse.class);

        VoucherEntity voucherEntity = voucherRepository.findByVoucherCode(voucherCode).orElseThrow(
                () -> new ResourceNotFoundException(VoucherEntity.class, voucherCode));
        VoucherDetailEntity voucherDetailEntity = voucherDetailRepository.findFirstByVoucherCodeAndStatus(voucherCode, VoucherStatusCode.READY_FOR_BUY)
                .orElseThrow(() -> new ResourceNotFoundException(VoucherDetailEntity.class, voucherCode));

        voucherDetailEntity.setCustomerCode(customerResponse.getCustomerCode());
        voucherDetailEntity.setStatus(VoucherStatusCode.BOUGHT);

        voucherDetailRepository.save(voucherDetailEntity);
        voucherEntity.setInactiveVoucher(voucherEntity.getInactiveVoucher() - 1);
        voucherRepository.save(voucherEntity);

        cmsWebClient.performUpdateCustomerInfo(BodyRequest.of(CustomerRequest.builder()
                        .customerCode(customerResponse.getCustomerCode())
                        .activeVoucher(customerResponse.getActiveVoucher() + 1)
                        .build()));

        return voucherMapper.entityToDTO(voucherEntity);
    }


}
