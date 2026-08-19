package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.ChiTietPhieuNhap;
import com.example.Bookshop.repository.ChiTietPhieuNhapRepository;
import com.example.Bookshop.service.ChiTietPhieuNhapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChiTietPhieuNhapServiceImpl extends AbstractCrudServiceImpl<ChiTietPhieuNhap, Integer> implements ChiTietPhieuNhapService {

    private final ChiTietPhieuNhapRepository repository;

    @Override
    protected JpaRepository<ChiTietPhieuNhap, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(ChiTietPhieuNhap entity, Integer id) {
        entity.setId(id);
    }
}
