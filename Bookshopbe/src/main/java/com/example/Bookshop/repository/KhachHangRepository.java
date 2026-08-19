package com.example.Bookshop.repository;

import com.example.Bookshop.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    Optional<KhachHang> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
