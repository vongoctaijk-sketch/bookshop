package com.example.Bookshop.service.impl;

import com.example.Bookshop.dto.HoaDonRequest;
import com.example.Bookshop.entity.ChiTietHoaDon;
import com.example.Bookshop.entity.HoaDon;
import com.example.Bookshop.entity.KhuyenMai;
import com.example.Bookshop.entity.NguoiDung;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.ChiTietHoaDonRepository;
import com.example.Bookshop.repository.HoaDonRepository;
import com.example.Bookshop.repository.KhuyenMaiRepository;
import com.example.Bookshop.repository.NguoiDungRepository;
import com.example.Bookshop.repository.SachRepository;
import com.example.Bookshop.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HoaDonServiceImpl extends AbstractCrudServiceImpl<HoaDon, Integer> implements HoaDonService {

    private final HoaDonRepository repository;
    private final KhuyenMaiRepository khuyenMaiRepository;
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
        hoaDon.setMaKhuyenMai(null);
        hoaDon.setTienGiam(BigDecimal.ZERO);

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
            if (sach.getSoLuongTon() == null || sach.getSoLuongTon() < item.getSoLuong()) {
                throw new IllegalArgumentException("Sách không đủ tồn kho: " + sach.getTenSach());
            }
            sach.setSoLuongTon(sach.getSoLuongTon() - item.getSoLuong());
            sachRepository.save(sach);

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

        BigDecimal tienGiam = applyPromotion(request.getMaKhuyenMai(), tongTien, hoaDon);
        hoaDon.setTongTien(tongTien.subtract(tienGiam));
        hoaDon.setTienGiam(tienGiam);
        hoaDon.setChiTietHoaDons(danhSachChiTiet);
        hoaDon.setDanhSachChiTiet(danhSachChiTiet);

        HoaDon savedHoaDon = repository.save(hoaDon);
        for (ChiTietHoaDon chiTiet : danhSachChiTiet) {
            chiTiet.setHoaDon(savedHoaDon);
            chiTietHoaDonRepository.save(chiTiet);
        }
        if (hoaDon.getMaKhuyenMai() != null) {
            KhuyenMai promotion = khuyenMaiRepository.findByMaKhuyenMaiIgnoreCase(hoaDon.getMaKhuyenMai())
                    .orElseThrow(() -> new IllegalStateException("Khuyến mãi không còn khả dụng"));
            promotion.setSoLuong(promotion.getSoLuong() - 1);
            khuyenMaiRepository.save(promotion);
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
                    String previousStatus = current.getTrangThai();
                    NguoiDung previousCustomer = current.getNguoiDung();
                    if (entity.getTrangThai() != null) current.setTrangThai(entity.getTrangThai());
                    if (entity.getTenNguoiNhan() != null) current.setTenNguoiNhan(entity.getTenNguoiNhan());
                    if (entity.getSdtNguoiNhan() != null) current.setSdtNguoiNhan(entity.getSdtNguoiNhan());
                    if (entity.getDiaChiGiaoHang() != null) current.setDiaChiGiaoHang(entity.getDiaChiGiaoHang());
                    if (entity.getTongTien() != null) current.setTongTien(entity.getTongTien());
                    if (entity.getMaHoaDon() != null) current.setMaHoaDon(entity.getMaHoaDon());
                    if (entity.getNgayBan() != null) current.setNgayBan(entity.getNgayBan());
                    if (entity.getMaKhuyenMai() != null) current.setMaKhuyenMai(entity.getMaKhuyenMai());
                    if (entity.getTienGiam() != null) current.setTienGiam(entity.getTienGiam());
                    if (entity.getNguoiDung() != null && entity.getNguoiDung().getId() != null) {
                        current.setNguoiDung(nguoiDungRepository.findById(entity.getNguoiDung().getId())
                                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại")));
                    }

                    validateHoaDon(current);
                    boolean wasCancelled = "CANCELLED".equalsIgnoreCase(previousStatus);
                    boolean isCancelled = "CANCELLED".equalsIgnoreCase(current.getTrangThai());
                    if (!wasCancelled && isCancelled) {
                        for (ChiTietHoaDon detail : chiTietHoaDonRepository.findAllByHoaDonId(id)) {
                            Sach book = detail.getSach();
                            book.setSoLuongTon(Math.addExact(book.getSoLuongTon(), detail.getSoLuong()));
                            sachRepository.save(book);
                        }
                        if (current.getMaKhuyenMai() != null) {
                            khuyenMaiRepository.findByMaKhuyenMaiIgnoreCase(current.getMaKhuyenMai()).ifPresent(promotion -> {
                                promotion.setSoLuong(promotion.getSoLuong() + 1);
                                khuyenMaiRepository.save(promotion);
                            });
                        }
                    }
                    HoaDon saved = repository.save(current);
                    updateLoyalty(previousCustomer, previousStatus, saved.getTrangThai(), saved.getTongTien());
                    return saved;
                });
    }

    private BigDecimal applyPromotion(String code, BigDecimal subtotal, HoaDon order) {
        if (code == null || code.isBlank()) return BigDecimal.ZERO;
        KhuyenMai promotion = khuyenMaiRepository.findByMaKhuyenMaiIgnoreCase(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Mã khuyến mãi không tồn tại"));
        LocalDate today = LocalDate.now();
        if (!Boolean.TRUE.equals(promotion.getDangHoatDong())
                || promotion.getSoLuong() == null || promotion.getSoLuong() <= 0
                || today.isBefore(promotion.getNgayBatDau()) || today.isAfter(promotion.getNgayKetThuc())) {
            throw new IllegalArgumentException("Mã khuyến mãi đã hết hạn hoặc hết lượt");
        }
        if (subtotal.compareTo(promotion.getDonHangToiThieu()) < 0) {
            throw new IllegalArgumentException("Đơn hàng chưa đạt giá trị tối thiểu của mã khuyến mãi");
        }
        BigDecimal discount = "PERCENT".equalsIgnoreCase(promotion.getLoaiGiam())
                ? subtotal.multiply(promotion.getGiaTri()).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP)
                : promotion.getGiaTri();
        if (discount.compareTo(subtotal) >= 0) {
            throw new IllegalArgumentException("Giá trị giảm không được bằng hoặc vượt tổng đơn hàng");
        }
        order.setMaKhuyenMai(promotion.getMaKhuyenMai());
        return discount;
    }

    private void updateLoyalty(NguoiDung customer, String previousStatus, String nextStatus, BigDecimal total) {
        if (customer == null || total == null) return;
        boolean wasCompleted = "COMPLETED".equalsIgnoreCase(previousStatus);
        boolean isCompleted = "COMPLETED".equalsIgnoreCase(nextStatus);
        if (wasCompleted == isCompleted) return;
        long earnedPoints = total.divide(BigDecimal.valueOf(10000), 0, java.math.RoundingMode.DOWN).longValue();
        long currentPoints = customer.getDiemTichLuy() == null ? 0 : customer.getDiemTichLuy();
        long points = Math.max(0, currentPoints + (isCompleted ? earnedPoints : -earnedPoints));
        customer.setDiemTichLuy(points);
        customer.setHangThanhVien(points >= 1000 ? "VIP" : points >= 500 ? "VANG" : points >= 100 ? "BAC" : "DONG");
        customer.setVip(points >= 1000);
        nguoiDungRepository.save(customer);
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
