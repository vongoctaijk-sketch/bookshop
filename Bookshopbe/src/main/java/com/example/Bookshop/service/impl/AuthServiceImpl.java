package com.example.Bookshop.service.impl;

import com.example.Bookshop.dto.LoginRequest;
import com.example.Bookshop.dto.LoginResponse;
import com.example.Bookshop.dto.RegisterRequest;
import com.example.Bookshop.entity.NguoiDung;
import com.example.Bookshop.entity.NhomNguoiDung;
import com.example.Bookshop.entity.RefreshToken;
import com.example.Bookshop.repository.NguoiDungRepository;
import com.example.Bookshop.repository.NhomNguoiDungRepository;
import com.example.Bookshop.repository.RefreshTokenRepository;
import com.example.Bookshop.security.JwtService;
import com.example.Bookshop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final NguoiDungRepository nguoiDungRepository;

    private final NhomNguoiDungRepository nhomNguoiDungRepository;
    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        String username = request.username();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return issueTokens(userDetails);
    }

    @Override
    @Transactional
    public RegisterRequest register(RegisterRequest request) {
        String email = request.getEmail() != null && !request.getEmail().isBlank()
                ? request.getEmail().trim()
                : (request.getUsername() != null ? request.getUsername().trim() : "");
        String username = request.getUsername() != null && !request.getUsername().isBlank()
                ? request.getUsername().trim()
                : email;
        String hoTen = request.getHoTen() != null && !request.getHoTen().isBlank()
                ? request.getHoTen().trim()
                : username;
        NhomNguoiDung Nhom= nhomNguoiDungRepository.findById("USER")
                .orElseThrow(() -> new IllegalArgumentException("Nhom nguoi dung khong ton tai"));

        if (email.isBlank()) {
            throw new IllegalArgumentException("Email khong duoc de trong");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password khong duoc de trong");
        }
        if (nguoiDungRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email da ton tai");
        }
        if (username != null && !username.isBlank() && nguoiDungRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("Username da ton tai");
        }



        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setUsername(username);
        nguoiDung.setEmail(email);
        nguoiDung.setHoTen(hoTen);
        nguoiDung.setPassword(passwordEncoder.encode(request.getPassword()));
        nguoiDung.setSdt(request.getSdt());
        nguoiDung.setDiaChi(request.getDiaChi());

        nguoiDung.setNhomNguoiDung(Nhom);
        nguoiDungRepository.save(nguoiDung);
        return request;
    }

    @Override
    @Transactional
    public LoginResponse refresh(String refreshToken) {
        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token khong hop le"));
        if (Boolean.TRUE.equals(savedToken.getRevoked()) || savedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token da het han hoac da bi thu hoi");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedToken.getUsername());
        savedToken.setRevoked(true);
        refreshTokenRepository.save(savedToken);
        return issueTokens(userDetails);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    private LoginResponse issueTokens(UserDetails userDetails) {
        revokeActiveRefreshTokens(userDetails.getUsername());

        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        String accessToken = jwtService.generateAccessToken(
                userDetails,
                Map.of("authorities", authorities)
        );
        String refreshToken = createRefreshToken(userDetails.getUsername());
        return new LoginResponse(accessToken, refreshToken, "Bearer", userDetails.getUsername(), authorities);
    }

    private String createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(LocalDateTime.now().plusNanos(refreshTokenExpirationMs * 1_000_000));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken).getToken();
    }

    private void revokeActiveRefreshTokens(String username) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUsernameAndRevokedFalse(username);
        activeTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(activeTokens);
    }
}
