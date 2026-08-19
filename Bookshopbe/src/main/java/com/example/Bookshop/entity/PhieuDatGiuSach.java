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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "phieu_dat_giu_sach")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuDatGiuSach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "trang_thai", length = 20)
    private String trangThai;

    @ManyToOne(optional = false)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    @JsonIgnoreProperties({"hoaDons", "phieuDatGiuSachs"})
    private KhachHang khachHang;

    @OneToMany(mappedBy = "holdOrder")
    @JsonIgnoreProperties({"holdOrder", "phieuGiu", "sach"})
    private List<ChiTietPhieuGiu> chiTietPhieuGius;
}
