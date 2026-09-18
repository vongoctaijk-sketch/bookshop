package com.example.Bookshop.service.impl;

import com.example.Bookshop.dto.HoaDonRequest;
import com.example.Bookshop.entity.ChiTietHoaDon;
import com.example.Bookshop.entity.HoaDon;
import com.example.Bookshop.entity.NguoiDung;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.ChiTietHoaDonRepository;
import com.example.Bookshop.repository.HoaDonRepository;
import com.example.Bookshop.repository.NguoiDungRepository;
import com.example.Bookshop.repository.SachRepository;
import com.example.Bookshop.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl extends AbstractCrudServiceImpl<HoaDon, Integer> implements HoaDonService {

    private final HoaDonRepository repository;
    private final NguoiDungRepository nguoiDungRepository;
    private final SachRepository sachRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;

    @Override
    protected JpaRepository<HoaDon, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(HoaDon entity, Integer id) {
        entity.setId(id);
    }

    @Override
    @Transactional
    public HoaDon create(HoaDonRequest request) {
        validateHoaDonRequest(request);

        NguoiDung nguoiDung = nguoiDungRepository.findById(request.getNguoiDungId())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMaHoaDon(generateMaHoaDon());
        hoaDon.setNgayBan(LocalDateTime.now());
        hoaDon.setTrangThai("PENDING");
        hoaDon.setNguoiDung(nguoiDung);
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        hoaDon.setSdtNguoiNhan(request.getSdtNguoiNhan());
        hoaDon.setDiaChiGiaoHang(request.getDiaChiGiaoHang());

        BigDecimal tongTien = BigDecimal.ZERO;
        List<ChiTietHoaDon> danhSachChiTiet = new ArrayList<>();

        for (HoaDonRequest.ChiTietHoaDonRequest item : request.getDanhSachChiTiet()) {
            Sach sach = sachRepository.findById(item.getSachId())
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + item.getSachId()));

            BigDecimal donGia = item.getDonGia() != null ? item.getDonGia() : sach.getGiaBan();
            if (donGia == null || donGia.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Đơn giá sách phải lớn hơn 0");
            }
            if (item.getSoLuong() == null || item.getSoLuong() <= 0) {
                throw new IllegalArgumentException("Số lượng sách phải lớn hơn 0");
            }

            ChiTietHoaDon chiTiet = new ChiTietHoaDon();
            chiTiet.setHoaDon(hoaDon);
            chiTiet.setSach(sach);
            chiTiet.setSoLuong(item.getSoLuong());
            chiTiet.setDonGia(donGia);
            chiTiet.setTenSach(sach.getTenSach());
            chiTiet.setHinhAnh(sach.getHinhAnh());
            chiTiet.setThanhTien(donGia.multiply(BigDecimal.valueOf(item.getSoLuong())));

            danhSachChiTiet.add(chiTiet);
            tongTien = tongTien.add(chiTiet.getThanhTien());
        }

        hoaDon.setTongTien(tongTien);
        hoaDon.setChiTietHoaDons(danhSachChiTiet);
        hoaDon.setDanhSachChiTiet(danhSachChiTiet);

        HoaDon savedHoaDon = repository.save(hoaDon);
        for (ChiTietHoaDon chiTiet : danhSachChiTiet) {
            chiTiet.setHoaDon(savedHoaDon);
            chiTietHoaDonRepository.save(chiTiet);
        }

        savedHoaDon.setChiTietHoaDons(danhSachChiTiet);
        savedHoaDon.setDanhSachChiTiet(danhSachChiTiet);
        return savedHoaDon;
    }

    @Override
    @Transactional
    public Optional<HoaDon> update(Integer id, HoaDon entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Dữ liệu hóa đơn không được null");
        }

        return repository.findById(id)
                .map(current -> {
                    validateHoaDon(entity);
                    resolveHoaDonReferences(entity);
                    entity.setId(id);
                    return repository.save(entity);
                });
    }

    public HoaDon resolveHoaDonReferences(HoaDon hoaDon) {
        if (hoaDon == null) {
            return null;
        }

        if (hoaDon.getNguoiDung() != null && hoaDon.getNguoiDung().getId() != null) {
            NguoiDung nguoiDung = nguoiDungRepository.findById(hoaDon.getNguoiDung().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
            hoaDon.setNguoiDung(nguoiDung);
        }

        return hoaDon;
    }

    private void validateHoaDonRequest(HoaDonRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu hóa đơn không được null");
        }
        if (request.getNguoiDungId() == null) {
            throw new IllegalArgumentException("Người dùng không được để trống");
        }
        if (request.getTenNguoiNhan() == null || request.getTenNguoiNhan().isBlank()) {
            throw new IllegalArgumentException("Tên người nhận không được để trống");
        }
        if (request.getSdtNguoiNhan() == null || request.getSdtNguoiNhan().isBlank()) {
            throw new IllegalArgumentException("Số điện thoại người nhận không được để trống");
        }
        if (request.getDiaChiGiaoHang() == null || request.getDiaChiGiaoHang().isBlank()) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống");
        }
        if (request.getDanhSachChiTiet() == null || request.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalArgumentException("Hóa đơn phải có ít nhất một sách");
        }
    }

    private void validateHoaDon(HoaDon hoaDon) {
        if (hoaDon.getMaHoaDon() == null || hoaDon.getMaHoaDon().isBlank()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống");
        }
        if (hoaDon.getNgayBan() == null) {
            throw new IllegalArgumentException("Ngày bán không được để trống");
        }
        if (hoaDon.getTongTien() == null || hoaDon.getTongTien().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tổng tiền phải lớn hơn 0");
        }
        if (hoaDon.getTrangThai() == null || hoaDon.getTrangThai().isBlank()) {
            throw new IllegalArgumentException("Trạng thái không được để trống");
        }
        if (hoaDon.getNguoiDung() == null || hoaDon.getNguoiDung().getId() == null) {
            throw new IllegalArgumentException("Người dùng không được để trống");
        }
        if (hoaDon.getTenNguoiNhan() == null || hoaDon.getTenNguoiNhan().isBlank()) {
            throw new IllegalArgumentException("Tên người nhận không được để trống");
        }
        if (hoaDon.getSdtNguoiNhan() == null || hoaDon.getSdtNguoiNhan().isBlank()) {
            throw new IllegalArgumentException("Số điện thoại người nhận không được để trống");
        }
        if (hoaDon.getDiaChiGiaoHang() == null || hoaDon.getDiaChiGiaoHang().isBlank()) {
            throw new IllegalArgumentException("Địa chỉ giao hàng không được để trống");
        }
    }

    private String generateMaHoaDon() {
        return "HD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
