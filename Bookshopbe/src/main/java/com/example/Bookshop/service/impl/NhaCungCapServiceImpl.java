package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.NhaCungCap;
import com.example.Bookshop.repository.NhaCungCapRepository;
import com.example.Bookshop.service.NhaCungCapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NhaCungCapServiceImpl extends AbstractCrudServiceImpl<NhaCungCap, Integer> implements NhaCungCapService {

    private final NhaCungCapRepository repository;

    @Override
    protected JpaRepository<NhaCungCap, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(NhaCungCap entity, Integer id) {
        entity.setId(id);
    }
}
