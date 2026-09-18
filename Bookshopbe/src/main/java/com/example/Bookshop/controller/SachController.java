package com.example.Bookshop.controller;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.service.SachService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/top-ban-chay")
    public ResponseEntity<List<Sach>> topSachBanChay() {
        return ResponseEntity.ok(sachService.topsachBanChayNhat());
    }
    @GetMapping
    public ResponseEntity<List<Sach>> getAll() {
        return ResponseEntity.ok(sachService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sach> getById(@PathVariable Integer id) {
        return sachService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
                .orElseGet(() -> ResponseEntity.notFound().build());
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
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!sachService.delete(id)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public Page<Sach> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer theLoaiId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return sachService.searchAndFilter(
                keyword,
                theLoaiId,
                page,
                size
        );
    }
}
