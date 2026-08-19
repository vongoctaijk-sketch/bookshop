package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.HoaDon;
import com.example.Bookshop.repository.HoaDonRepository;
import com.example.Bookshop.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl extends AbstractCrudServiceImpl<HoaDon, Integer> implements HoaDonService {

    private final HoaDonRepository repository;

    @Override
    protected JpaRepository<HoaDon, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(HoaDon entity, Integer id) {
        entity.setId(id);
    }
}
