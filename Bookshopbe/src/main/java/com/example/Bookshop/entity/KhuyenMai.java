package com.example.Bookshop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "khuyen_mai")
@Getter
@Setter
@NoArgsConstructor
public class KhuyenMai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "ma_khuyen_mai", nullable = false, unique = true)
    private String maKhuyenMai;

    @NotBlank
    @Column(name = "ten_khuyen_mai", nullable = false)
    private String tenKhuyenMai;

    @NotBlank
    @Column(name = "loai_giam", nullable = false)
    private String loaiGiam;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "gia_tri", nullable = false)
    private BigDecimal giaTri;

    @NotNull
    @PositiveOrZero
    @Column(name = "don_hang_toi_thieu", nullable = false)
    private BigDecimal donHangToiThieu = BigDecimal.ZERO;

    @NotNull
    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @NotNull
    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @NotNull
    @PositiveOrZero
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong = 1;

    @NotNull
    @Column(name = "dang_hoat_dong", nullable = false)
    private Boolean dangHoatDong = true;
}
