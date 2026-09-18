package com.example.Bookshop.repository;

import com.example.Bookshop.entity.Sach;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SachRepository extends JpaRepository<Sach, Integer>, JpaSpecificationExecutor<Sach> {
    @Query("""
    SELECT c.sach
    FROM ChiTietHoaDon c
    GROUP BY c.sach
    ORDER BY SUM(c.soLuong) DESC
""")
    List<Sach> topSachBanChayNhat(Pageable pageable);

}
