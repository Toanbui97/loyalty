package vn.com.vpbanks.loyalty.core.mapper;

import org.mapstruct.Mapper;
import vn.com.vpbanks.loyalty.core.dto.request.CustomerRequest;
import vn.com.vpbanks.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.vpbanks.loyalty.core.entity.CustomerEntity;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse entityToDTO(CustomerEntity customerEntity);
    CustomerEntity DTOToEntity(CustomerRequest customerRequest);
}
