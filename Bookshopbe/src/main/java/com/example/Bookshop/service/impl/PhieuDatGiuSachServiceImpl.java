package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.PhieuDatGiuSach;
import com.example.Bookshop.repository.PhieuDatGiuSachRepository;
import com.example.Bookshop.service.PhieuDatGiuSachService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhieuDatGiuSachServiceImpl extends AbstractCrudServiceImpl<PhieuDatGiuSach, Integer> implements PhieuDatGiuSachService {

    private final PhieuDatGiuSachRepository repository;

    @Override
    protected JpaRepository<PhieuDatGiuSach, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(PhieuDatGiuSach entity, Integer id) {
        entity.setId(id);
    }
}
