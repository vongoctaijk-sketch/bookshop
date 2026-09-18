package com.example.Bookshop.service.impl;

import com.example.Bookshop.entity.ChiTietHoaDon;
import com.example.Bookshop.entity.HoaDon;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.ChiTietHoaDonRepository;
import com.example.Bookshop.repository.HoaDonRepository;
import com.example.Bookshop.repository.SachRepository;
import com.example.Bookshop.service.ChiTietHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChiTietHoaDonServiceImpl extends AbstractCrudServiceImpl<ChiTietHoaDon, Integer> implements ChiTietHoaDonService {

    private final ChiTietHoaDonRepository repository;
    private final HoaDonRepository hoaDonRepository;
    private final SachRepository sachRepository;

    @Override
    protected JpaRepository<ChiTietHoaDon, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(ChiTietHoaDon entity, Integer id) {
        entity.setId(id);
    }

    @Override
    public ChiTietHoaDon create(ChiTietHoaDon entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu chi tiết hóa đơn không được null");
        }
        validateChiTietHoaDon(entity);
        resolveChiTietHoaDonReferences(entity);
        return repository.save(entity);
    }

    @Override
    public Optional<ChiTietHoaDon> update(Integer id, ChiTietHoaDon entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu chi tiết hóa đơn không được null");
        }

        return repository.findById(id)
                .map(current -> {
                    validateChiTietHoaDon(entity);
                    resolveChiTietHoaDonReferences(entity);
                    entity.setId(id);
                    return repository.save(entity);
                });
    }

    public ChiTietHoaDon resolveChiTietHoaDonReferences(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon == null) {
            return null;
        }

        if (chiTietHoaDon.getHoaDon() != null && chiTietHoaDon.getHoaDon().getId() != null) {
            HoaDon hoaDon = hoaDonRepository.findById(chiTietHoaDon.getHoaDon().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Hóa đơn không tồn tại"));
            chiTietHoaDon.setHoaDon(hoaDon);
        }

        if (chiTietHoaDon.getSach() != null && chiTietHoaDon.getSach().getId() != null) {
            Sach sach = sachRepository.findById(chiTietHoaDon.getSach().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại"));
            chiTietHoaDon.setSach(sach);
            if (chiTietHoaDon.getTenSach() == null || chiTietHoaDon.getTenSach().isBlank()) {
                chiTietHoaDon.setTenSach(sach.getTenSach());
            }
            if (chiTietHoaDon.getHinhAnh() == null || chiTietHoaDon.getHinhAnh().isBlank()) {
                chiTietHoaDon.setHinhAnh(sach.getHinhAnh());
            }
            if (chiTietHoaDon.getDonGia() == null || chiTietHoaDon.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
                chiTietHoaDon.setDonGia(sach.getGiaBan());
            }
        }

        if (chiTietHoaDon.getDonGia() != null && chiTietHoaDon.getSoLuong() != null) {
            chiTietHoaDon.setThanhTien(chiTietHoaDon.getDonGia().multiply(BigDecimal.valueOf(chiTietHoaDon.getSoLuong())));
        }

        return chiTietHoaDon;
    }

    private void validateChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) {
        if (chiTietHoaDon.getSoLuong() == null || chiTietHoaDon.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (chiTietHoaDon.getDonGia() == null || chiTietHoaDon.getDonGia().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Đơn giá phải lớn hơn 0");
        }
        if (chiTietHoaDon.getThanhTien() == null || chiTietHoaDon.getThanhTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Thành tiền phải lớn hơn 0");
        }
        if (chiTietHoaDon.getHoaDon() == null || chiTietHoaDon.getHoaDon().getId() == null) {
            throw new IllegalArgumentException("Hóa đơn không được để trống");
        }
        if (chiTietHoaDon.getSach() == null || chiTietHoaDon.getSach().getId() == null) {
            throw new IllegalArgumentException("Sách không được để trống");
        }
        if (chiTietHoaDon.getThanhTien().compareTo(chiTietHoaDon.getDonGia().multiply(BigDecimal.valueOf(chiTietHoaDon.getSoLuong()))) != 0) {
            throw new IllegalArgumentException("Thành tiền không khớp với số lượng và đơn giá");
        }
    }
}
