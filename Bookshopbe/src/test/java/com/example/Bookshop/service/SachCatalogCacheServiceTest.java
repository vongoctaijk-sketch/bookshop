package com.example.Bookshop.service;

import com.example.Bookshop.dto.CachedSachPage;
import com.example.Bookshop.dto.CatalogSachDto;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.mapper.CatalogSachMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit tests cho SachCatalogCacheService: kiểm tra logic chuyển đổi dữ liệu,
 * không khởi động Spring và không kiểm tra cơ chế cache của annotation.
 */
class SachCatalogCacheServiceTest {

    private final SachService sachService = mock(SachService.class);
    private final CatalogSachMapper catalogSachMapper = mock(CatalogSachMapper.class);
    private final SachCatalogCacheService service =
            new SachCatalogCacheService(sachService, catalogSachMapper);

    @Test
    void getAllMapsEveryBookToCatalogDto() {
        // Arrange: giả lập kết quả từ service nghiệp vụ và kết quả mapping.
        Sach firstBook = new Sach();
        Sach secondBook = new Sach();
        CatalogSachDto firstDto = new CatalogSachDto();
        CatalogSachDto secondDto = new CatalogSachDto();
        when(sachService.getAll()).thenReturn(List.of(firstBook, secondBook));
        when(catalogSachMapper.toDto(firstBook)).thenReturn(firstDto);
        when(catalogSachMapper.toDto(secondBook)).thenReturn(secondDto);

        // Act: gọi trực tiếp method, không đi qua Spring cache proxy.
        List<CatalogSachDto> result = service.getAll();

        // Assert: đảm bảo mỗi sách được chuyển thành DTO và giữ đúng thứ tự.
        assertEquals(List.of(firstDto, secondDto), result);
        verify(sachService).getAll();
        verify(catalogSachMapper).toDto(firstBook);
        verify(catalogSachMapper).toDto(secondBook);
    }

    @Test
    void getByIdReturnsMappedDtoWhenBookExists() {
        // Arrange.
        Sach book = new Sach();
        CatalogSachDto expectedDto = new CatalogSachDto();
        when(sachService.getById(12)).thenReturn(Optional.of(book));
        when(catalogSachMapper.toDto(book)).thenReturn(expectedDto);

        // Act.
        CatalogSachDto result = service.getById(12);

        // Assert: test này chỉ kiểm tra logic tìm thấy và mapping.
        assertEquals(expectedDto, result);
        verify(sachService).getById(12);
        verify(catalogSachMapper).toDto(book);
    }

    @Test
    void getByIdReturnsNullAndDoesNotMapWhenBookIsMissing() {
        // Arrange: nghiệp vụ biểu diễn không tìm thấy bằng Optional rỗng.
        when(sachService.getById(404)).thenReturn(Optional.empty());

        // Act.
        CatalogSachDto result = service.getById(404);

        // Assert.
        assertNull(result);
        verify(sachService).getById(404);
        verifyNoInteractions(catalogSachMapper);
    }

    @Test
    void getTopSellingMapsEveryBookToCatalogDto() {
        // Arrange.
        Sach popularBook = new Sach();
        CatalogSachDto popularDto = new CatalogSachDto();
        when(sachService.topsachBanChayNhat()).thenReturn(List.of(popularBook));
        when(catalogSachMapper.toDto(popularBook)).thenReturn(popularDto);

        // Act.
        List<CatalogSachDto> result = service.getTopSelling();

        // Assert.
        assertEquals(List.of(popularDto), result);
        verify(sachService).topsachBanChayNhat();
        verify(catalogSachMapper).toDto(popularBook);
    }

    @Test
    void searchMapsPageContentAndCopiesPaginationMetadata() {
        // Arrange: PageImpl đại diện một trang dữ liệu có metadata giống Spring Data.
        Sach firstBook = new Sach();
        Sach secondBook = new Sach();
        Sach thirdBook = new Sach();
        CatalogSachDto firstBookDto = new CatalogSachDto();
        CatalogSachDto secondBookDto = new CatalogSachDto();
        CatalogSachDto thirdBookDto = new CatalogSachDto();
        Page<Sach> sachPage = new PageImpl<>(
                List.of(firstBook, secondBook, thirdBook),
                PageRequest.of(2, 5),
                13
        );
        when(sachService.searchAndFilter("java", 3, 2, 5)).thenReturn(sachPage);
        when(catalogSachMapper.toDto(firstBook)).thenReturn(firstBookDto);
        when(catalogSachMapper.toDto(secondBook)).thenReturn(secondBookDto);
        when(catalogSachMapper.toDto(thirdBook)).thenReturn(thirdBookDto);

        // Act.
        CachedSachPage result = service.search("java", 3, 2, 5);

        // Assert: vừa kiểm tra nội dung, vừa kiểm tra page/size/tổng số bản ghi.
        assertEquals(List.of(firstBookDto, secondBookDto, thirdBookDto), result.getContent());
        assertEquals(2, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals(13L, result.getTotalElements());
        verify(sachService).searchAndFilter("java", 3, 2, 5);
        verify(catalogSachMapper).toDto(firstBook);
        verify(catalogSachMapper).toDto(secondBook);
        verify(catalogSachMapper).toDto(thirdBook);
    }
}
