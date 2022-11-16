package vn.com.vpbanks.loyalty.core.mapper;

import org.mapstruct.Mapper;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.vpbanks.loyalty.core.entity.VoucherDetailEntity;

@Mapper(componentModel = "spring")
public interface VoucherDetailMapper {

    VoucherDetailResponse entityToDTO(VoucherDetailEntity entity);
}
