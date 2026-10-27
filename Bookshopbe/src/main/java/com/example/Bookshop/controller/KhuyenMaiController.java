package com.example.Bookshop.controller;

import com.example.Bookshop.entity.KhuyenMai;
import com.example.Bookshop.exception.ResourceNotFoundException;
import com.example.Bookshop.repository.KhuyenMaiRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khuyen-mai")
@RequiredArgsConstructor
public class KhuyenMaiController {
    private final KhuyenMaiRepository repository;

    @GetMapping
    public List<KhuyenMai> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public KhuyenMai getById(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public KhuyenMai create(@Valid @RequestBody KhuyenMai promotion) {
        validate(promotion, null);
        promotion.setId(null);
        return repository.save(promotion);
    }

    @PutMapping("/{id}")
    public KhuyenMai update(@PathVariable Integer id, @Valid @RequestBody KhuyenMai promotion) {
        KhuyenMai current = repository.findById(id).orElseThrow(ResourceNotFoundException::new);
        validate(promotion, id);
        promotion.setId(current.getId());
        return repository.save(promotion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        if (!repository.existsById(id)) throw new ResourceNotFoundException();
        repository.deleteById(id);
    }

    private void validate(KhuyenMai promotion, Integer currentId) {
        promotion.setMaKhuyenMai(promotion.getMaKhuyenMai().trim().toUpperCase());
        String type = promotion.getLoaiGiam() == null ? "" : promotion.getLoaiGiam().toUpperCase();
        if (!"PERCENT".equals(type) && !"FIXED".equals(type)) {
            throw new IllegalArgumentException("Loại giảm phải là PERCENT hoặc FIXED");
        }
        if ("PERCENT".equals(type) && promotion.getGiaTri().compareTo(java.math.BigDecimal.valueOf(100)) >= 0) {
            throw new IllegalArgumentException("Mức giảm phần trăm phải nhỏ hơn 100");
        }
        if (promotion.getNgayKetThuc().isBefore(promotion.getNgayBatDau())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu");
        }
        KhuyenMai existing = repository.findByMaKhuyenMaiIgnoreCase(promotion.getMaKhuyenMai()).orElse(null);
        if (existing != null && !existing.getId().equals(currentId)) {
            throw new IllegalArgumentException("Mã khuyến mãi đã tồn tại");
        }
        promotion.setLoaiGiam(type);
    }
}
