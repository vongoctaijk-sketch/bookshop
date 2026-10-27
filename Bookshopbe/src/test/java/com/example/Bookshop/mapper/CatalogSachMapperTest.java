package com.example.Bookshop.mapper;

import com.example.Bookshop.dto.CatalogSachDto;
import com.example.Bookshop.entity.Sach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CatalogSachMapperTest {

    private final CatalogSachMapper mapper = Mappers.getMapper(CatalogSachMapper.class);

    @Test
    void mapsBookFieldsAndDefaultsAuthorsToAnEmptyList() {
        Sach sach = new Sach();
        sach.setId(12);
        sach.setTenSach("Clean Code");
        sach.setTacGias(null);

        CatalogSachDto dto = mapper.toDto(sach);

        assertEquals(12, dto.getId());
        assertEquals("Clean Code", dto.getTenSach());
        assertNotNull(dto.getTacGias());
        assertTrue(dto.getTacGias().isEmpty());
    }
}
