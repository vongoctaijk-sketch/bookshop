package com.example.Bookshop.configs;

import com.example.Bookshop.security.JwtAuthenticationFilter;
import com.example.Bookshop.security.Permission;
import com.example.Bookshop.security.BookshopPasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/nguoi-dung/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh", "/api/auth/logout").permitAll()
                        .requestMatchers("/api/ai/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Public shop read endpoints
                        .requestMatchers(HttpMethod.POST, "/api/nguoi-dung/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sach/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/the-loai/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tac-gia/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/nha-xuat-ban/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/nha-cung-cap/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/chi-tiet-hoa-don/**").hasAuthority(Permission.CHI_TIET_HOA_DON_READ)
                        .requestMatchers(HttpMethod.POST, "/api/chi-tiet-hoa-don/**").hasAuthority(Permission.CHI_TIET_HOA_DON_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/chi-tiet-hoa-don/**").hasAuthority(Permission.CHI_TIET_HOA_DON_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/chi-tiet-hoa-don/**").hasAuthority(Permission.CHI_TIET_HOA_DON_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/chi-tiet-phieu-giu/*" +
                                "*").hasAuthority(Permission.CHI_TIET_PHIEU_GIU_READ)
                        .requestMatchers(HttpMethod.POST, "/api/chi-tiet-phieu-giu/**").hasAuthority(Permission.CHI_TIET_PHIEU_GIU_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/chi-tiet-phieu-giu/**").hasAuthority(Permission.CHI_TIET_PHIEU_GIU_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/chi-tiet-phieu-giu/**").hasAuthority(Permission.CHI_TIET_PHIEU_GIU_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/chi-tiet-phieu-nhap/**").hasAuthority(Permission.CHI_TIET_PHIEU_NHAP_READ)
                        .requestMatchers(HttpMethod.POST, "/api/chi-tiet-phieu-nhap/**").hasAuthority(Permission.CHI_TIET_PHIEU_NHAP_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/chi-tiet-phieu-nhap/**").hasAuthority(Permission.CHI_TIET_PHIEU_NHAP_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/chi-tiet-phieu-nhap/**").hasAuthority(Permission.CHI_TIET_PHIEU_NHAP_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/chuc-nang/**").hasAuthority(Permission.CHUC_NANG_READ)
                        .requestMatchers(HttpMethod.POST, "/api/chuc-nang/**").hasAuthority(Permission.CHUC_NANG_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/chuc-nang/**").hasAuthority(Permission.CHUC_NANG_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/chuc-nang/**").hasAuthority(Permission.CHUC_NANG_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/hoa-don/**").hasAuthority(Permission.HOA_DON_READ)
                        .requestMatchers(HttpMethod.POST, "/api/hoa-don/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/hoa-don/**").hasAuthority(Permission.HOA_DON_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/hoa-don/**").hasAuthority(Permission.HOA_DON_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/khach-hang/**").hasAuthority(Permission.KHACH_HANG_READ)
                        .requestMatchers(HttpMethod.POST, "/api/khach-hang/**").hasAuthority(Permission.KHACH_HANG_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/khach-hang/**").hasAuthority(Permission.KHACH_HANG_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/khach-hang/**").hasAuthority(Permission.KHACH_HANG_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/nguoi-dung/**").hasAuthority(Permission.NGUOI_DUNG_READ)

                        .requestMatchers(HttpMethod.PUT, "/api/nguoi-dung/**").hasAuthority(Permission.NGUOI_DUNG_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/nguoi-dung/**").hasAuthority(Permission.NGUOI_DUNG_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/nha-cung-cap/**").hasAuthority(Permission.NHA_CUNG_CAP_READ)
                        .requestMatchers(HttpMethod.POST, "/api/nha-cung-cap/**").hasAuthority(Permission.NHA_CUNG_CAP_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/nha-cung-cap/**").hasAuthority(Permission.NHA_CUNG_CAP_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/nha-cung-cap/**").hasAuthority(Permission.NHA_CUNG_CAP_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/nha-xuat-ban/**").hasAuthority(Permission.NHA_XUAT_BAN_READ)
                        .requestMatchers(HttpMethod.POST, "/api/nha-xuat-ban/**").hasAuthority(Permission.NHA_XUAT_BAN_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/nha-xuat-ban/**").hasAuthority(Permission.NHA_XUAT_BAN_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/nha-xuat-ban/**").hasAuthority(Permission.NHA_XUAT_BAN_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/nhom-nguoi-dung/**").hasAuthority(Permission.NHOM_NGUOI_DUNG_READ)
                        .requestMatchers(HttpMethod.POST, "/api/nhom-nguoi-dung/**").hasAuthority(Permission.NHOM_NGUOI_DUNG_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/nhom-nguoi-dung/**").hasAuthority(Permission.NHOM_NGUOI_DUNG_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/nhom-nguoi-dung/**").hasAuthority(Permission.NHOM_NGUOI_DUNG_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/phieu-dat-giu-sach/**").hasAuthority(Permission.PHIEU_DAT_GIU_SACH_READ)
                        .requestMatchers(HttpMethod.POST, "/api/phieu-dat-giu-sach/**").hasAuthority(Permission.PHIEU_DAT_GIU_SACH_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/phieu-dat-giu-sach/**").hasAuthority(Permission.PHIEU_DAT_GIU_SACH_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/phieu-dat-giu-sach/**").hasAuthority(Permission.PHIEU_DAT_GIU_SACH_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/phieu-nhap/**").hasAuthority(Permission.PHIEU_NHAP_READ)
                        .requestMatchers(HttpMethod.POST, "/api/phieu-nhap/**").hasAuthority(Permission.PHIEU_NHAP_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/phieu-nhap/**").hasAuthority(Permission.PHIEU_NHAP_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/phieu-nhap/**").hasAuthority(Permission.PHIEU_NHAP_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/refresh-token/**").hasAuthority(Permission.REFRESH_TOKEN_READ)
                        .requestMatchers(HttpMethod.POST, "/api/refresh-token/**").hasAuthority(Permission.REFRESH_TOKEN_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/refresh-token/**").hasAuthority(Permission.REFRESH_TOKEN_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/refresh-token/**").hasAuthority(Permission.REFRESH_TOKEN_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/sach/**").hasAuthority(Permission.SACH_READ)
                        .requestMatchers(HttpMethod.POST, "/api/sach/**").hasAuthority(Permission.SACH_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/sach/**").hasAuthority(Permission.SACH_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/sach/**").hasAuthority(Permission.SACH_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/tac-gia/**").hasAuthority(Permission.TAC_GIA_READ)
                        .requestMatchers(HttpMethod.POST, "/api/tac-gia/**").hasAuthority(Permission.TAC_GIA_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/tac-gia/**").hasAuthority(Permission.TAC_GIA_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/tac-gia/**").hasAuthority(Permission.TAC_GIA_DELETE)
                        .requestMatchers(HttpMethod.GET, "/api/the-loai/**").hasAuthority(Permission.THE_LOAI_READ)
                        .requestMatchers(HttpMethod.POST, "/api/the-loai/**").hasAuthority(Permission.THE_LOAI_CREATE)
                        .requestMatchers(HttpMethod.PUT, "/api/the-loai/**").hasAuthority(Permission.THE_LOAI_UPDATE)
                        .requestMatchers(HttpMethod.DELETE, "/api/the-loai/**").hasAuthority(Permission.THE_LOAI_DELETE)
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BookshopPasswordEncoder();
    }
}
