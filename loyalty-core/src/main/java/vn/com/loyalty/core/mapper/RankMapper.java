package vn.com.loyalty.core.mapper;

import org.mapstruct.Mapper;
import vn.com.loyalty.core.dto.request.RankRequest;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.entity.cms.RankEntity;

@Mapper(componentModel = "spring")
public interface RankMapper {

    RankEntity DTOToEntity(RankRequest rankRequest);

    RankResponse entityToDTO(RankEntity rankEntity);

    RankEntity DTOToEntity(RankResponse rankResponse);
}

