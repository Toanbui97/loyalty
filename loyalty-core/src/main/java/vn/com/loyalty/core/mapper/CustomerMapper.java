package vn.com.loyalty.core.mapper;

import org.mapstruct.Mapper;
import vn.com.loyalty.core.dto.request.CustomerRequest;
import vn.com.loyalty.core.dto.response.cms.CustomerResponse;
import vn.com.loyalty.core.entity.CustomerEntity;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponse entityToDTO(CustomerEntity customerEntity);
    CustomerEntity DTOToEntity(CustomerRequest customerRequest);
}
