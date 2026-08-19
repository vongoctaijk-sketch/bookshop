package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.TheLoai;
import com.example.Bookshop.repository.TheLoaiRepository;
import com.example.Bookshop.service.TheLoaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TheLoaiServiceImpl extends AbstractCrudServiceImpl<TheLoai, Integer> implements TheLoaiService {

    private final TheLoaiRepository repository;

    @Override
    protected JpaRepository<TheLoai, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(TheLoai entity, Integer id) {
        entity.setId(id);
    }
}
