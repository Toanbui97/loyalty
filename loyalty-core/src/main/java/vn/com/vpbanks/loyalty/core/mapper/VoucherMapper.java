package vn.com.vpbanks.loyalty.core.mapper;

import org.mapstruct.Mapper;
import vn.com.vpbanks.loyalty.core.dto.request.VoucherRequest;
import vn.com.vpbanks.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.vpbanks.loyalty.core.entity.VoucherEntity;

@Mapper(componentModel = "spring")
public interface VoucherMapper {

    VoucherResponse entityToDTO(VoucherEntity entity);
    VoucherEntity DTOToEntity(VoucherRequest request);
}
