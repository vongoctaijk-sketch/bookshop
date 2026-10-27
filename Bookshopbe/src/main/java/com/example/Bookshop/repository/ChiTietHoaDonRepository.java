package com.example.Bookshop.repository;

import com.example.Bookshop.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, Integer> {
    java.util.List<ChiTietHoaDon> findAllByHoaDonId(Integer hoaDonId);
}
