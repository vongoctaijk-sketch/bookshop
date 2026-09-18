package com.example.Bookshop.service;

import com.example.Bookshop.entity.Sach;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface SachService extends CrudService<Sach, Integer> {
    Page<Sach> searchAndFilter(
            String keyword,
            Integer theLoaiId,
            int page,
            int size
    );

    Sach create(Sach sach, MultipartFile fileAnh);
    List<Sach> topsachBanChayNhat();
    Optional<Sach> update(Integer id, Sach sach, MultipartFile fileAnh);
}
