package vn.com.loyalty.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vn.com.loyalty.core.dto.request.RankRequest;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.entity.cms.RankEntity;

@Mapper(componentModel = "spring"
        , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT
        , nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
        , nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
        , nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
        , nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface RankMapper {

    RankEntity DTOToEntity(RankRequest rankRequest);

    RankResponse entityToDTO(RankEntity rankEntity);

    RankEntity DTOToEntity(RankResponse rankResponse);
}

