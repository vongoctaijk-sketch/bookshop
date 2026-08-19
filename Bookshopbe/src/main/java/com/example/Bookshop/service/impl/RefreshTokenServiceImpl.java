package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.RefreshToken;
import com.example.Bookshop.repository.RefreshTokenRepository;
import com.example.Bookshop.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl extends AbstractCrudServiceImpl<RefreshToken, Long> implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Override
    protected JpaRepository<RefreshToken, Long> getRepository() {
        return repository;
    }

    @Override
    protected void setId(RefreshToken entity, Long id) {
        entity.setId(id);
    }


}
