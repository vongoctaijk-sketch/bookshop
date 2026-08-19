package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.ChiTietHoaDon;
import com.example.Bookshop.repository.ChiTietHoaDonRepository;
import com.example.Bookshop.service.ChiTietHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChiTietHoaDonServiceImpl extends AbstractCrudServiceImpl<ChiTietHoaDon, Integer> implements ChiTietHoaDonService {

    private final ChiTietHoaDonRepository repository;

    @Override
    protected JpaRepository<ChiTietHoaDon, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(ChiTietHoaDon entity, Integer id) {
        entity.setId(id);
    }
}
