package com.example.Bookshop.security;

import com.example.Bookshop.entity.ChucNang;
import com.example.Bookshop.entity.NguoiDung;
import com.example.Bookshop.entity.NhomNguoiDung;
import com.example.Bookshop.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookshopUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository repository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String loginValue) throws UsernameNotFoundException {
        String identifier = loginValue == null ? "" : loginValue.trim();
        if (identifier.isEmpty()) {
            throw new UsernameNotFoundException("Khong tim thay nguoi dung: " + loginValue);
        }

        NguoiDung nguoiDung = repository.findByEmailIgnoreCase(identifier)
                .or(() -> repository.findByUsernameIgnoreCase(identifier))
                .or(() -> repository.findByHoTenIgnoreCase(identifier))
                .orElseThrow(() -> new UsernameNotFoundException("Khong tim thay nguoi dung: " + identifier));

        String principal = nguoiDung.getEmail() != null && !nguoiDung.getEmail().isBlank()
                ? nguoiDung.getEmail()
                : (nguoiDung.getUsername() != null ? nguoiDung.getUsername() : nguoiDung.getHoTen());

        return User.builder()
                .username(principal)
                .password(Objects.toString(nguoiDung.getPassword(), ""))
                .authorities(authoritiesOf(nguoiDung))
                .build();
    }

    private List<GrantedAuthority> authoritiesOf(NguoiDung nguoiDung) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        NhomNguoiDung nhom = nguoiDung.getNhomNguoiDung();
        if (nhom != null) {
            addAuthority(authorities, "ROLE_" + nhom.getTenNhom());
            if (nhom.getPhanQuyens() != null) {
                for (com.example.Bookshop.entity.PhanQuyen phanQuyen : nhom.getPhanQuyens()) {
                    ChucNang chucNang = phanQuyen.getChucNang();
                    addAuthority(authorities, chucNang != null ? chucNang.getTenChucNang() : null);
                }
            }
        }

        return authorities;
    }

    private void addAuthority(List<GrantedAuthority> authorities, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        String normalized = normalizeAuthority(value);
        if (normalized.isBlank()) {
            return;
        }
        authorities.add(new SimpleGrantedAuthority(normalized));
    }

    private String normalizeAuthority(String value) {
        String withoutMarks = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return withoutMarks
                .trim()
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }
}
