package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.NhaXuatBan;
import com.example.Bookshop.repository.NhaXuatBanRepository;
import com.example.Bookshop.service.NhaXuatBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NhaXuatBanServiceImpl extends AbstractCrudServiceImpl<NhaXuatBan, Integer> implements NhaXuatBanService {

    private final NhaXuatBanRepository repository;

    @Override
    protected JpaRepository<NhaXuatBan, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(NhaXuatBan entity, Integer id) {
        entity.setId(id);
    }
}
