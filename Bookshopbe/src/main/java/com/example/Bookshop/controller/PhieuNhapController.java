package com.example.Bookshop.controller;

import com.example.Bookshop.entity.PhieuNhap;
import com.example.Bookshop.dto.PhieuNhapRequest;
import com.example.Bookshop.exception.ResourceNotFoundException;
import com.example.Bookshop.service.PhieuNhapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phieu-nhap")
@RequiredArgsConstructor
public class PhieuNhapController {

    private final PhieuNhapService service;

    @GetMapping
    public ResponseEntity<List<PhieuNhap>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhieuNhap> getById(@PathVariable Integer id) {
        return service.getById(id).map(ResponseEntity::ok).orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    public ResponseEntity<PhieuNhap> create(@Valid @RequestBody PhieuNhapRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createFromRequest(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PhieuNhap> update(@PathVariable Integer id, @Valid @RequestBody PhieuNhapRequest request) {
        return ResponseEntity.ok(service.updateFromRequest(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<PhieuNhap> cancel(@PathVariable Integer id) {
        return ResponseEntity.ok(service.cancel(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
