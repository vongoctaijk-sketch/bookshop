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

    @Column(name = "ten_sach")
    private String tenSach;

    @Column(name = "gia_ban")
    private BigDecimal giaBan;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "hinh_anh")
    private String hinhAnh;

    @Column(name = "nam_xuat_ban")
    private Integer namXuatBan;

    @Column(name = "dang_kinh_doanh", nullable = false)
    private Boolean dangKinhDoanh;

    @ManyToOne
    @JoinColumn(name = "the_loai_id")
    @JsonIgnoreProperties({"danhSachSach"})
    private TheLoai theLoai;

    @ManyToOne
    @JoinColumn(name = "nha_xuat_ban_id")
    @JsonIgnoreProperties({"danhSachSach"})
    private NhaXuatBan nhaXuatBan;

    @ManyToMany
    @JoinTable(
            name = "sach_tac_gia",
            joinColumns = @JoinColumn(name = "sach_id"),
            inverseJoinColumns = @JoinColumn(name = "tac_gia_id")
    )
    @JsonIgnoreProperties({"danhSachSach"})
    private List<TacGia> tacGias;
}
