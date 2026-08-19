package com.example.Bookshop.controller;

import com.example.Bookshop.entity.ChiTietPhieuNhap;
import com.example.Bookshop.service.ChiTietPhieuNhapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chi-tiet-phieu-nhap")
@RequiredArgsConstructor
public class ChiTietPhieuNhapController {

    private final ChiTietPhieuNhapService service;

    @GetMapping
    public ResponseEntity<List<ChiTietPhieuNhap>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChiTietPhieuNhap> getById(@PathVariable Integer id) {
        return service.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ChiTietPhieuNhap> create(@RequestBody ChiTietPhieuNhap entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChiTietPhieuNhap> update(@PathVariable Integer id, @RequestBody ChiTietPhieuNhap entity) {
        return service.update(id, entity).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
