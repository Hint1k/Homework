package com.demo.finance.domain.mapper;

import com.demo.finance.domain.utils.Type;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {

    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "type", source = "type", qualifiedByName = "typeToString")
    TransactionDto toDto(Transaction transaction);

    @Mapping(target = "transactionId", source = "transactionId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToType")
    Transaction toEntity(TransactionDto transactionDto);

    @Named("typeToString")
    default String typeToString(Type type) {
        return type != null ? type.name() : null;
    }

    @Named("stringToType")
    default Type stringToType(String type) {
        return type != null ? Type.valueOf(type) : null;
    }
}