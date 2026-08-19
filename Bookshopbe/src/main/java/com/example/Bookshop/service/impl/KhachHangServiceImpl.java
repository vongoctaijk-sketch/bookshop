package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.KhachHang;
import com.example.Bookshop.repository.KhachHangRepository;
import com.example.Bookshop.service.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KhachHangServiceImpl extends AbstractCrudServiceImpl<KhachHang, Integer> implements KhachHangService {

    private final KhachHangRepository repository;

    @Override
    protected JpaRepository<KhachHang, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(KhachHang entity, Integer id) {
        entity.setId(id);
    }
}
