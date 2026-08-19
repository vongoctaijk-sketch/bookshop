package com.example.Bookshop.service;

import com.example.Bookshop.entity.Sach;
import org.springframework.data.domain.Page;

public interface SachService extends CrudService<Sach, Integer> {
    Page<Sach> searchAndFilter(
            String keyword,
            Integer theLoaiId,
            int page,
            int size
    );
}
