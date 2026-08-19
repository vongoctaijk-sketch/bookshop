package com.example.Bookshop.repository;

import com.example.Bookshop.entity.Sach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SachRepository extends JpaRepository<Sach, Integer>, JpaSpecificationExecutor<Sach> {
}
