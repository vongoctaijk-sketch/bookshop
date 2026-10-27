package com.example.Bookshop.service;

import com.example.Bookshop.dto.CatalogSachDto;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.mapper.CatalogSachMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests cho phần tích hợp giữa @Cacheable và Spring Cache.
 * Dùng Spring context nhỏ cùng cache trong bộ nhớ, không cần Redis hay database.
 */
@SpringJUnitConfig(SachCatalogCacheServiceIntegrationTest.CacheTestConfiguration.class)
class SachCatalogCacheServiceIntegrationTest {

    @Autowired
    private SachCatalogCacheService service;

    @Autowired
    private SachService sachService;

    @Autowired
    private CatalogSachMapper catalogSachMapper;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCachesAndMocks() {
        // Mỗi test bắt đầu với cache và mock sạch để không phụ thuộc thứ tự chạy.
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        reset(sachService, catalogSachMapper);
    }

    @Test
    void doesNotCacheNullLookupResults() {
        // Arrange: sách không tồn tại, do đó method trả về null.
        when(sachService.getById(404)).thenReturn(Optional.empty());

        // Act.
        service.getById(404);
        service.getById(404);

        // Assert: unless = "#result == null" khiến cả hai lần đều gọi nghiệp vụ.
        verify(sachService, times(2)).getById(404);
    }

    @Test
    void cachesNonNullLookupResult() {
        // Arrange.
        CatalogSachDto expectedDto = new CatalogSachDto();
        when(sachService.getById(13)).thenReturn(Optional.of(new Sach()));
        when(catalogSachMapper.toDto(any(Sach.class)))
                .thenReturn(expectedDto);

        // Act.
        CatalogSachDto firstCall = service.getById(13);
        CatalogSachDto secondCall = service.getById(13);

        // Assert: kết quả cache là cùng DTO, backend chỉ được gọi đúng một lần.
        assertSame(expectedDto, firstCall);
        assertSame(firstCall, secondCall);
        verify(sachService, times(1)).getById(13);
    }

    @Configuration
    @EnableCaching
    @Import(SachCatalogCacheService.class)
    static class CacheTestConfiguration {

        @Bean
        SachService sachService() {
            return mock(SachService.class);
        }

        @Bean
        CatalogSachMapper catalogSachMapper() {
            return mock(CatalogSachMapper.class);
        }

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(
                    "catalog-books",
                    "catalog-book-by-id",
                    "catalog-top-selling",
                    "catalog-search"
            );
        }
    }
}
