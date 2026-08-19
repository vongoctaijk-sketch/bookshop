package com.example.Bookshop.repository;

import com.example.Bookshop.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    Optional<NguoiDung> findByUsername(String username);
    Optional<NguoiDung> findByUsernameIgnoreCase(String username);
    Optional<NguoiDung> findByEmail(String email);
    Optional<NguoiDung> findByEmailIgnoreCase(String email);
    Optional<NguoiDung> findByHoTenIgnoreCase(String hoTen);
    boolean existsByUsername(String username);
    boolean existsByUsernameIgnoreCase(String username);
    boolean existsByEmailIgnoreCase(String email);
}
