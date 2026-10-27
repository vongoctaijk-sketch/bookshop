package com.example.Bookshop.service;

import com.example.Bookshop.dto.CachedSachPage;
import com.example.Bookshop.dto.CatalogSachDto;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.mapper.CatalogSachMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SachCatalogCacheService {

    private final SachService sachService;
    private final CatalogSachMapper catalogSachMapper;

    @Cacheable(cacheNames = "catalog-books")
    public List<CatalogSachDto> getAll() {
        return sachService.getAll().stream().map(catalogSachMapper::toDto).toList();
    }

    @Cacheable(cacheNames = "catalog-book-by-id", key = "#p0", unless = "#result == null")
    public CatalogSachDto getById(Integer id) {
        return sachService.getById(id).map(catalogSachMapper::toDto).orElse(null);
    }

    @Cacheable(cacheNames = "catalog-top-selling")
    public List<CatalogSachDto> getTopSelling() {
        return sachService.topsachBanChayNhat().stream().map(catalogSachMapper::toDto).toList();
    }

    @Cacheable(cacheNames = "catalog-search", key = "#p0 + ':' + #p1 + ':' + #p2 + ':' + #p3")
    public CachedSachPage search(String keyword, Integer theLoaiId, int page, int size) {
        Page<Sach> result = sachService.searchAndFilter(keyword, theLoaiId, page, size);
        List<CatalogSachDto> content = result.getContent().stream().map(catalogSachMapper::toDto).toList();
        return new CachedSachPage(content, result.getNumber(),
                result.getSize(), result.getTotalElements());
    }
}