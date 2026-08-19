package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.ChiTietPhieuGiu;
import com.example.Bookshop.repository.ChiTietPhieuGiuRepository;
import com.example.Bookshop.service.ChiTietPhieuGiuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChiTietPhieuGiuServiceImpl extends AbstractCrudServiceImpl<ChiTietPhieuGiu, Integer> implements ChiTietPhieuGiuService {

    private final ChiTietPhieuGiuRepository repository;

    @Override
    protected JpaRepository<ChiTietPhieuGiu, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(ChiTietPhieuGiu entity, Integer id) {
        entity.setId(id);
    }
}
