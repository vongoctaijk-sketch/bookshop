package com.example.Bookshop.controller;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.dto.CachedSachPage;
import com.example.Bookshop.dto.CatalogSachDto;
import com.example.Bookshop.exception.ResourceNotFoundException;
import com.example.Bookshop.service.SachCatalogCacheService;
import com.example.Bookshop.service.SachService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/sach")
@RequiredArgsConstructor
public class SachController {

    private final SachService sachService;
    private final SachCatalogCacheService catalogCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/top-ban-chay")
    public ResponseEntity<List<CatalogSachDto>> topSachBanChay() {
        return ResponseEntity.ok(catalogCacheService.getTopSelling());
    }
    @GetMapping
    public ResponseEntity<List<CatalogSachDto>> getAll() {
        return ResponseEntity.ok(catalogCacheService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogSachDto> getById(@PathVariable Integer id) {
        CatalogSachDto sach = catalogCacheService.getById(id);
        if (sach == null) {
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(sach);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Sach> create(@Valid @RequestBody Sach sach) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sachService.create(sach));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Sach> createWithImage(
            @RequestPart("sach") String sachJson,
            @RequestPart(value = "fileAnh", required = false) MultipartFile fileAnh
    ) throws JsonProcessingException {
        Sach sach = objectMapper.readValue(sachJson, Sach.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(sachService.create(sach, fileAnh));
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Sach> update(@PathVariable Integer id, @Valid @RequestBody Sach sach) {
        return sachService.update(id, sach)
                .map(ResponseEntity::ok)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Sach> updateWithImage(
            @PathVariable Integer id,
            @RequestPart("sach") String sachJson,
            @RequestPart(value = "fileAnh", required = false) MultipartFile fileAnh
    ) throws JsonProcessingException {
        Sach sach = objectMapper.readValue(sachJson, Sach.class);
        return sachService.update(id, sach, fileAnh)
                .map(ResponseEntity::ok)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!sachService.delete(id)) {
            throw new ResourceNotFoundException();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<CatalogSachDto> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer theLoaiId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        CachedSachPage cachedPage = catalogCacheService.search(keyword, theLoaiId, page, size);
        return new PageImpl<>(cachedPage.getContent(),
            PageRequest.of(cachedPage.getPage(), cachedPage.getSize()), cachedPage.getTotalElements());
    }
}
