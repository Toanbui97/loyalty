package vn.com.loyalty.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import vn.com.loyalty.core.dto.message.TransactionOrchestrationReq;
import vn.com.loyalty.core.entity.transaction.TransactionEntity;

@Mapper(componentModel = "spring"
        , nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT
        , nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
        , nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
        , nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT
        , nullValueIterableMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface TransactionOrchestrationMapper {

    TransactionOrchestrationReq entityToDTO(TransactionEntity transaction);

}
