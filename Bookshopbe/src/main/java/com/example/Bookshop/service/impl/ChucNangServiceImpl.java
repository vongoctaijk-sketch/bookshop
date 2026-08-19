package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.ChucNang;
import com.example.Bookshop.repository.ChucNangRepository;
import com.example.Bookshop.service.ChucNangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChucNangServiceImpl extends AbstractCrudServiceImpl<ChucNang, Integer> implements ChucNangService {

    private final ChucNangRepository repository;

    @Override
    protected JpaRepository<ChucNang, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(ChucNang entity, Integer id) {
        entity.setMaChucNang(id);
    }
}
