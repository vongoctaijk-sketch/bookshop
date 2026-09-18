package com.example.Bookshop.service.impl;

import com.example.Bookshop.dto.AdminCreateUserRequest;
import com.example.Bookshop.entity.NguoiDung;
import com.example.Bookshop.entity.NhomNguoiDung;
import com.example.Bookshop.repository.NguoiDungRepository;
import com.example.Bookshop.repository.NhomNguoiDungRepository;
import com.example.Bookshop.security.BookshopPasswordEncoder;
import com.example.Bookshop.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImpl extends AbstractCrudServiceImpl<NguoiDung, Integer> implements NguoiDungService {

    private final NguoiDungRepository repository;

    private final NhomNguoiDungRepository nhomNguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    protected JpaRepository<NguoiDung, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(NguoiDung entity, Integer id) {
        entity.setId(id);
    }

    @Override
    public NguoiDung create(NguoiDung entity) {
        encodePasswordIfNeeded(entity);
        return super.create(entity);
    }

    @Override
    public Optional<NguoiDung> update(Integer id, NguoiDung entity) {
        if (entity.getPassword() == null || entity.getPassword().isBlank()) {
            repository.findById(id).ifPresent(current -> entity.setPassword(current.getPassword()));
        } else {
            encodePasswordIfNeeded(entity);
        }
        return super.update(id, entity);
    }

    @Override
    @Transactional
    public NguoiDung createByAdmin(AdminCreateUserRequest request) {
        String username = request.getUsername() == null ? "" : request.getUsername().trim();
        String email = request.getEmail() == null ? "" : request.getEmail().trim();
        String tenNhom = request.getTenNhom() == null ? "" : request.getTenNhom().trim();

        if (username.isBlank() || email.isBlank() || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Username, email va password khong duoc de trong");
        }
        if (repository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username da ton tai");
        }
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email da ton tai");
        }

        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setUsername(username);
        nguoiDung.setEmail(email);
        nguoiDung.setHoTen(request.getHoTen());
        nguoiDung.setPassword(passwordEncoder.encode(request.getPassword()));
        nguoiDung.setSdt(request.getSdt());
        nguoiDung.setDiaChi(request.getDiaChi());

        if (!tenNhom.isBlank()) {
            NhomNguoiDung nhom = nhomNguoiDungRepository.findById(tenNhom)
                    .orElseThrow(() -> new IllegalArgumentException("Nhom nguoi dung khong ton tai: " + tenNhom));
            nguoiDung.setNhomNguoiDung(nhom);
        }



        return repository.save(nguoiDung);
    }

    private void encodePasswordIfNeeded(NguoiDung entity) {
        String password = entity.getPassword();
        if (password == null || password.isBlank()) {
            return;
        }
        if (passwordEncoder instanceof BookshopPasswordEncoder encoder && encoder.isEncoded(password)) {
            return;
        }
        entity.setPassword(passwordEncoder.encode(password));
    }
}
