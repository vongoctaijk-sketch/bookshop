package com.example.Bookshop.repository;

import com.example.Bookshop.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {
    boolean existsByMaKhuyenMaiIgnoreCase(String maKhuyenMai);
    java.util.Optional<KhuyenMai> findByMaKhuyenMaiIgnoreCase(String maKhuyenMai);
}
