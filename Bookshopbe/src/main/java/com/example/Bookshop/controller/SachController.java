package com.example.Bookshop.controller;

import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.service.SachService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sach")
@RequiredArgsConstructor
public class SachController {

    private final SachService sachService;

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

    @PostMapping
    public ResponseEntity<Sach> create(@RequestBody Sach sach) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sachService.create(sach));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sach> update(@PathVariable Integer id, @RequestBody Sach sach) {
        return sachService.update(id, sach)
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
