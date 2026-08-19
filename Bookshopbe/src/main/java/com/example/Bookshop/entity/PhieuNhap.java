package com.example.Bookshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "phieu_nhap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ngay_nhap")
    private LocalDateTime ngayNhap;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @ManyToOne
    @JoinColumn(name = "nha_cung_cap_id")
    @JsonIgnoreProperties({"phieuNhaps"})
    private NhaCungCap nhaCungCap;

    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id")
    @JsonIgnoreProperties({"hoaDons", "phieuNhaps", "nhomNguoiDung"})
    private NguoiDung nguoiDung;

    @OneToMany(mappedBy = "phieuNhap")
    @JsonIgnoreProperties({"phieuNhap", "sach"})
    private List<ChiTietPhieuNhap> chiTietPhieuNhaps;

    @OneToMany
    @JoinTable(
            name = "phieu_nhap_danh_sach_chi_tiet",
            joinColumns = @JoinColumn(name = "phieu_nhap_id"),
            inverseJoinColumns = @JoinColumn(name = "danh_sach_chi_tiet_id")
    )
    @JsonIgnoreProperties({"phieuNhap", "sach"})
    private List<ChiTietPhieuNhap> danhSachChiTiet;
}
