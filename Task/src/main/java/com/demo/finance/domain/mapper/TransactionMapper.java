package com.demo.finance.domain.mapper;

import com.demo.finance.domain.utils.Type;
import com.demo.finance.domain.dto.TransactionDto;
import com.demo.finance.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * The {@code TransactionMapper} interface defines methods for mapping between {@link Transaction} entities
 * and {@link TransactionDto} data transfer objects using MapStruct. It provides bidirectional conversion
 * capabilities to facilitate the transformation of transaction-related data between the application's
 * persistence layer and its API layer.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    /**
     * Converts a {@link Transaction} entity into a {@link TransactionDto} data transfer object.
     * The {@code type} field is mapped from an enum to its string representation.
     *
     * @param transaction the {@link Transaction} entity to map
     * @return the corresponding {@link TransactionDto} object
     */
     @Mapping(target = "type", source = "type", qualifiedByName = "typeToString")
    TransactionDto toDto(Transaction transaction);

    /**
     * Converts a {@link TransactionDto} data transfer object into a {@link Transaction} entity.
     * The {@code type} field is mapped from a string to its corresponding enum value.
     *
     * @param transactionDto the {@link TransactionDto} object to map
     * @return the corresponding {@link Transaction} entity
     */
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToType")
    Transaction toEntity(TransactionDto transactionDto);

    /**
     * Converts a {@link Type} enum value to its string representation.
     *
     * @param type the {@link Type} enum value to convert
     * @return the string representation of the enum, or {@code null} if the input is {@code null}
     */
    @Named("typeToString")
    default String typeToString(Type type) {
        return type != null ? type.name() : null;
    }

    /**
     * Converts a string representation of a transaction type into its corresponding {@link Type} enum value.
     *
     * @param type the string representation of the transaction type
     * @return the corresponding {@link Type} enum value, or {@code null} if the input is {@code null}
     */
    @Named("stringToType")
    default Type stringToType(String type) {
        return type != null ? Type.valueOf(type.toUpperCase()) : null;
    }
}