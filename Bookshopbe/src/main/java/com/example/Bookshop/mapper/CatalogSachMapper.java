package com.example.Bookshop.mapper;

import com.example.Bookshop.dto.CatalogSachDto;
import com.example.Bookshop.entity.Sach;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CatalogSachMapper {

    @Mapping(target = "tacGias", defaultExpression = "java(java.util.List.of())")
    CatalogSachDto toDto(Sach sach);
}
