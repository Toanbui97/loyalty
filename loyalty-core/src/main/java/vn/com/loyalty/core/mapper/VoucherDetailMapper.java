package vn.com.loyalty.core.mapper;

import org.mapstruct.Mapper;
import vn.com.loyalty.core.dto.response.voucher.VoucherDetailResponse;
import vn.com.loyalty.core.entity.voucher.VoucherDetailEntity;

@Mapper(componentModel = "spring")
public interface VoucherDetailMapper {

    VoucherDetailResponse entityToDTO(VoucherDetailEntity entity);
}
