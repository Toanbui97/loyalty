package vn.com.loyalty.core.mapper;

import org.mapstruct.Mapper;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.core.entity.VoucherEntity;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    VoucherResponse entityToDTO(VoucherEntity entity);
    VoucherEntity DTOToEntity(VoucherRequest request);
}
