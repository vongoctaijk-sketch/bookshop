package com.example.Bookshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "sach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank(message = "Tên sách không được để trống")
    @Column(name = "ten_sach")
    private String tenSach;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    @Column(name = "gia_ban")
    private BigDecimal giaBan;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn không được âm")
    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @NotNull(message = "Năm xuất bản không được để trống")
    @Min(value = 1900, message = "Năm xuất bản phải từ 1900 trở lên")
    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @NotNull(message = "Trạng thái kinh doanh không được để trống")
    @Column(name = "dang_kinh_doanh", nullable = false)
    private Boolean dangKinhDoanh;

    @NotNull(message = "Thể loại không được để trống")
    @ManyToOne
    @JoinColumn(name = "the_loai_id")
    @JsonIgnoreProperties({"danhSachSach"})
    private TheLoai theLoai;

    @NotNull(message = "Nhà xuất bản không được để trống")
    @ManyToOne
    @JoinColumn(name = "nha_xuat_ban_id")
    @JsonIgnoreProperties({"danhSachSach"})
    private NhaXuatBan nhaXuatBan;

    @NotEmpty(message = "Sách phải có ít nhất một tác giả")
    @ManyToMany
    @JoinTable(
            name = "sach_tac_gia",
            joinColumns = @JoinColumn(name = "sach_id"),
            inverseJoinColumns = @JoinColumn(name = "tac_gia_id")
    )
    @JsonIgnoreProperties({"danhSachSach"})
    private List<TacGia> tacGias;
}
