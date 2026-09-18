package com.example.Bookshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "sdt")
    private String sdt;
    @Column(name = "dia_chi")
    private String diaChi;




    @ManyToOne
    @JoinColumn(name = "ten_nhom")
    @JsonIgnoreProperties({"nguoiDungs"})
    private NhomNguoiDung nhomNguoiDung;

    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnoreProperties({"nguoiDung", "danhSachChiTiet"})
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "nguoiDung")
    @JsonIgnoreProperties({"nguoiDung", "nhaCungCap", "danhSachChiTiet"})
    private List<PhieuNhap> phieuNhaps;
}
