package com.example.Bookshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "nhom_nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhomNguoiDung {

    @Id
    @Column(name = "ten_nhom")
    private String tenNhom;

    @OneToMany(mappedBy = "nhomNguoiDung")
    @JsonIgnoreProperties({"nhomNguoiDung"})
    private List<NguoiDung> nguoiDungs;

    @OneToMany(mappedBy = "nhomNguoiDung", fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"nhomNguoiDung"})
    private List<PhanQuyen> phanQuyens;
}
