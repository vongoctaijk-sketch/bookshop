package com.example.Bookshop.repository;

import com.example.Bookshop.entity.PhanQuyen;
import com.example.Bookshop.entity.PhanQuyenId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhanQuyenRepository extends JpaRepository<PhanQuyen, PhanQuyenId> {
}
