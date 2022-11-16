package vn.com.vpbanks.loyalty.core.service.internal.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.com.vpbanks.loyalty.core.constant.Constants;
import vn.com.vpbanks.loyalty.core.constant.enums.StatusCode;
import vn.com.vpbanks.loyalty.core.dto.request.BaseRequest;
import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.vpbanks.loyalty.core.entity.CustomerEntity;
import vn.com.vpbanks.loyalty.core.entity.VoucherDetailEntity;
import vn.com.vpbanks.loyalty.core.entity.VoucherEntity;
import vn.com.vpbanks.loyalty.core.exception.ResourceNotFoundException;
import vn.com.vpbanks.loyalty.core.mapper.VoucherMapper;
import vn.com.vpbanks.loyalty.core.repository.VoucherDetailRepository;
import vn.com.vpbanks.loyalty.core.repository.VoucherRepository;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherDetailService;
import vn.com.vpbanks.loyalty.core.service.internal.VoucherService;
import vn.com.vpbanks.loyalty.core.thirdparty.cms.service.CmsWebClient;
import vn.com.vpbanks.loyalty.core.utils.RequestUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoucherResponse activeVoucher(String voucherCode) {

        String customerCode = RequestUtil.extractCustomerCodeFromToken(httpRequest.getHeader(Constants.AUTH_HEADER));
        CustomerResponse customerResponse = cmsWebClient.receiveCustomerInfo(BaseRequest.of(CustomerRequest.builder().customerCode(customerCode).build())).getData();

        VoucherEntity voucherEntity = voucherRepository.findByVoucherCode(voucherCode).orElseThrow(
                () -> new ResourceNotFoundException(VoucherEntity.class.getName(), voucherCode));
        VoucherDetailEntity voucherDetailEntity = voucherDetailRepository.findFirstByVoucherCodeAndStatus(voucherCode, StatusCode.INACTIVE.getCode())
                .orElseThrow(() -> new ResourceNotFoundException(VoucherDetailEntity.class.getName(), voucherCode));

        voucherDetailEntity.setCustomerCode(customerResponse.getCustomerCode());
        voucherDetailEntity.setStatus(StatusCode.ACTIVE);

        voucherDetailRepository.save(voucherDetailEntity);
        voucherEntity.setInactiveVoucher(voucherEntity.getInactiveVoucher() - 1);
        voucherRepository.save(voucherEntity);

        cmsWebClient.performUpdateCustomerInfo(BaseRequest.of(CustomerRequest.builder()
                        .customerCode(customerResponse.getCustomerCode())
                        .activeVoucher(customerResponse.getActiveVouchers() + 1)
                        .build()));

        return voucherMapper.entityToDTO(voucherEntity);
    }


}
