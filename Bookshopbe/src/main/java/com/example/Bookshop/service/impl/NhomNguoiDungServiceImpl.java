package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.NhomNguoiDung;
import com.example.Bookshop.repository.NhomNguoiDungRepository;
import com.example.Bookshop.service.NhomNguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NhomNguoiDungServiceImpl extends AbstractCrudServiceImpl<NhomNguoiDung, String> implements NhomNguoiDungService {

    private final NhomNguoiDungRepository repository;

    @Override
    protected JpaRepository<NhomNguoiDung, String> getRepository() {
        return repository;
    }

    @Override
    protected void setId(NhomNguoiDung entity, String id) {
        if (id != null) {
            entity.setTenNhom(id);
        }
    }
}
