package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.PhieuNhap;
import com.example.Bookshop.repository.PhieuNhapRepository;
import com.example.Bookshop.service.PhieuNhapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhieuNhapServiceImpl extends AbstractCrudServiceImpl<PhieuNhap, Integer> implements PhieuNhapService {

    private final PhieuNhapRepository repository;

    @Override
    protected JpaRepository<PhieuNhap, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(PhieuNhap entity, Integer id) {
        entity.setId(id);
    }
}
