package com.example.Bookshop.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "nhom_chuc_nang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhanQuyen {

    @EmbeddedId
    private PhanQuyenId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tenNhom")
    @JoinColumn(name = "ten_nhom")
    private NhomNguoiDung nhomNguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maChucNang")
    @JoinColumn(name = "ma_chuc_nang")
    private ChucNang chucNang;

    public PhanQuyen(String tenNhom, Integer maChucNang) {
        this.id = new PhanQuyenId(tenNhom, maChucNang);
    }
}
