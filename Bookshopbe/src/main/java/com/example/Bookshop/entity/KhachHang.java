package com.example.Bookshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "khachHang")
    @JsonIgnoreProperties({"khachHang", "nguoiDung", "danhSachChiTiet"})
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "khachHang")
    @JsonIgnoreProperties({"khachHang", "chiTietPhieuGius"})
    private List<PhieuDatGiuSach> phieuDatGiuSachs;
}
