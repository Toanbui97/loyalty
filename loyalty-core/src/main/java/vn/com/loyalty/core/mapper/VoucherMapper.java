package vn.com.loyalty.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vn.com.loyalty.core.dto.message.VoucherOrchestrationReq;
import vn.com.loyalty.core.dto.request.VoucherMessageReq;
import vn.com.loyalty.core.dto.request.VoucherRequest;
import vn.com.loyalty.core.dto.response.voucher.VoucherResponse;
import vn.com.loyalty.core.entity.voucher.VoucherEntity;

@Mapper(componentModel = "spring"
        , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT
        , nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
        , nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
        , nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
        , nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface VoucherMapper {

    VoucherResponse entityToDTO(VoucherEntity entity);
    VoucherEntity DTOToEntity(VoucherRequest request);
    VoucherOrchestrationReq DTOToOrchestrationReq(VoucherMessageReq request);
}
