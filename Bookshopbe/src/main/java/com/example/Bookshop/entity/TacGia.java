package com.example.Bookshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "tac_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TacGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ho_ten")
    private String hoTen;

    @Column(name = "quoc_tich")
    private String quocTich;

    @Column(name = "tieu_su", columnDefinition = "TEXT")
    private String tieuSu;

    @ManyToMany(mappedBy = "tacGias")
    @JsonIgnoreProperties({"tacGias", "theLoai", "nhaXuatBan"})
    private List<Sach> danhSachSach;
}
