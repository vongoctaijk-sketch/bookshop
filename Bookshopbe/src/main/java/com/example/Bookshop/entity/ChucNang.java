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
@Table(name = "chuc_nang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChucNang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chuc_nang")
    private Integer maChucNang;

    @Column(name = "ten_chuc_nang")
    private String tenChucNang;

    @OneToMany(mappedBy = "chucNang")
    @JsonIgnoreProperties({"chucNang", "nhomNguoiDung"})
    private List<PhanQuyen> phanQuyens;
}
