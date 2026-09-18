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
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @NotNull(message = "Đơn giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Đơn giá phải lớn hơn 0")
    @Column(name = "don_gia")
    private BigDecimal donGia;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "ten_sach")
    private String tenSach;

    @NotNull(message = "Thành tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Thành tiền phải lớn hơn 0")
    @Column(name = "thanh_tien")
    private BigDecimal thanhTien;

    @NotNull(message = "Hóa đơn không được để trống")
    @ManyToOne
    @JoinColumn(name = "hoa_don_id")
    @JsonIgnoreProperties({"chiTietHoaDons", "danhSachChiTiet"})
    private HoaDon hoaDon;

    @NotNull(message = "Sách không được để trống")
    @ManyToOne
    @JoinColumn(name = "sach_id")
    @JsonIgnoreProperties({"tacGias", "theLoai", "nhaXuatBan"})
    private Sach sach;
}
