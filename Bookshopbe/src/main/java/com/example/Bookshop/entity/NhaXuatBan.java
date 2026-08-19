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
@Table(name = "nha_xuat_ban")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NhaXuatBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tennxb")
    private String tenNxb;

    @Column(name = "dia_chi")
    private String diaChi;

    @OneToMany(mappedBy = "nhaXuatBan")
    @JsonIgnoreProperties({"theLoai", "nhaXuatBan", "tacGias"})
    private List<Sach> danhSachSach;
}
