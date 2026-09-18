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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Mã hóa đơn không được để trống")
    @Column(name = "ma_hoa_don")
    private String maHoaDon;

    @NotNull(message = "Ngày bán không được để trống")
    @Column(name = "ngay_ban")
    private LocalDateTime ngayBan;

    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Tổng tiền phải lớn hơn 0")
    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @NotBlank(message = "Trạng thái không được để trống")
    @Column(name = "trang_thai")
    private String trangThai;

    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne
    @JoinColumn(name = "nguoi_dung_id")
    @JsonIgnoreProperties({"hoaDons", "phieuNhaps", "nhomNguoiDung"})
    private NguoiDung nguoiDung;

    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;

    @Column(name = "sdt_nguoi_nhan")
    private String sdtNguoiNhan;

    @Column(name = "dia_chi_giao_hang")
    private String diaChiGiaoHang;

    @OneToMany(mappedBy = "hoaDon")
    @JsonIgnoreProperties({"hoaDon", "sach"})
    private List<ChiTietHoaDon> chiTietHoaDons;

    @OneToMany
    @JoinTable(
            name = "hoa_don_danh_sach_chi_tiet",
            joinColumns = @JoinColumn(name = "hoa_don_id"),
            inverseJoinColumns = @JoinColumn(name = "danh_sach_chi_tiet_id")
    )
    @JsonIgnoreProperties({"hoaDon", "sach"})
    private List<ChiTietHoaDon> danhSachChiTiet;
}
