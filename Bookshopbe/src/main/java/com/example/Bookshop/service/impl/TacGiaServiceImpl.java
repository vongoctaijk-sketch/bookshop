package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.TacGia;
import com.example.Bookshop.repository.TacGiaRepository;
import com.example.Bookshop.service.TacGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TacGiaServiceImpl extends AbstractCrudServiceImpl<TacGia, Integer> implements TacGiaService {

    private final TacGiaRepository repository;

    @Override
    protected JpaRepository<TacGia, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(TacGia entity, Integer id) {
        entity.setId(id);
    }
}
