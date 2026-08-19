package com.example.Bookshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_phieu_nhap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietPhieuNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "gia_nhap")
    private BigDecimal giaNhap;

    @ManyToOne
    @JoinColumn(name = "phieu_nhap_id")
    @JsonIgnoreProperties({"chiTietPhieuNhaps", "danhSachChiTiet"})
    private PhieuNhap phieuNhap;

    @ManyToOne
    @JoinColumn(name = "sach_id")
    @JsonIgnoreProperties({"tacGias", "theLoai", "nhaXuatBan"})
    private Sach sach;
}
