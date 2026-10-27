package com.example.Bookshop.service.impl;

import com.example.Bookshop.dto.PhieuNhapRequest;
import com.example.Bookshop.entity.ChiTietPhieuNhap;
import com.example.Bookshop.entity.NguoiDung;
import com.example.Bookshop.entity.NhaCungCap;
import com.example.Bookshop.entity.PhieuNhap;
import com.example.Bookshop.entity.Sach;
import com.example.Bookshop.repository.ChiTietPhieuNhapRepository;
import com.example.Bookshop.repository.NguoiDungRepository;
import com.example.Bookshop.repository.NhaCungCapRepository;
import com.example.Bookshop.repository.PhieuNhapRepository;
import com.example.Bookshop.repository.SachRepository;
import com.example.Bookshop.service.PhieuNhapService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhieuNhapServiceImpl extends AbstractCrudServiceImpl<PhieuNhap, Integer> implements PhieuNhapService {

    private final PhieuNhapRepository repository;
    private final ChiTietPhieuNhapRepository chiTietRepository;
    private final SachRepository sachRepository;
    private final NhaCungCapRepository nhaCungCapRepository;
    private final NguoiDungRepository nguoiDungRepository;

    @Override
    protected JpaRepository<PhieuNhap, Integer> getRepository() {
        return repository;
    }

    @Override
    protected void setId(PhieuNhap entity, Integer id) {
        entity.setId(id);
    }

    @Override
    @Transactional
    public PhieuNhap createFromRequest(PhieuNhapRequest request) {
        PhieuNhap phieuNhap = new PhieuNhap();
        phieuNhap.setNgayNhap(LocalDateTime.now());
        phieuNhap.setTrangThai("HOAN_THANH");
        applyRequest(phieuNhap, request);
        return saveWithDetails(phieuNhap, request);
    }

    @Override
    @Transactional
    public PhieuNhap updateFromRequest(Integer id, PhieuNhapRequest request) {
        PhieuNhap current = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu nhập không tồn tại"));
        if ("HUY".equals(current.getTrangThai())) {
            throw new IllegalStateException("Không thể cập nhật phiếu nhập đã hủy");
        }
        reverseStock(current);
        chiTietRepository.deleteAll(chiTietRepository.findAllByPhieuNhapId(id));
        applyRequest(current, request);
        return saveWithDetails(current, request);
    }

    @Override
    @Transactional
    public PhieuNhap cancel(Integer id) {
        PhieuNhap current = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Phiếu nhập không tồn tại"));
        if (!"HUY".equals(current.getTrangThai())) {
            reverseStock(current);
            current.setTrangThai("HUY");
            repository.save(current);
        }
        return current;
    }

    private void applyRequest(PhieuNhap phieuNhap, PhieuNhapRequest request) {
        if (request == null || request.getChiTietPhieuNhaps() == null || request.getChiTietPhieuNhaps().isEmpty()) {
            throw new IllegalArgumentException("Phiếu nhập phải có ít nhất một đầu sách");
        }
        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(request.getNhaCungCapId())
                .orElseThrow(() -> new IllegalArgumentException("Nhà cung cấp không tồn tại"));
        NguoiDung nguoiDung = nguoiDungRepository.findById(request.getNguoiDungId())
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại"));
        phieuNhap.setNhaCungCap(nhaCungCap);
        phieuNhap.setNguoiDung(nguoiDung);
        phieuNhap.setTrangThai("HOAN_THANH");
    }

    private PhieuNhap saveWithDetails(PhieuNhap phieuNhap, PhieuNhapRequest request) {
        BigDecimal total = BigDecimal.ZERO;
        List<ChiTietPhieuNhap> details = new ArrayList<>();
        for (PhieuNhapRequest.ChiTietRequest item : request.getChiTietPhieuNhaps()) {
            if (item.getSoLuong() == null || item.getSoLuong() <= 0 || item.getGiaNhap() == null
                    || item.getGiaNhap().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Số lượng phải dương và giá nhập không được âm");
            }
            Sach sach = sachRepository.findById(item.getSachId())
                    .orElseThrow(() -> new IllegalArgumentException("Sách không tồn tại: " + item.getSachId()));
            ChiTietPhieuNhap detail = new ChiTietPhieuNhap();
            detail.setPhieuNhap(phieuNhap);
            detail.setSach(sach);
            detail.setSoLuong(item.getSoLuong());
            detail.setGiaNhap(item.getGiaNhap());
            total = total.add(item.getGiaNhap().multiply(BigDecimal.valueOf(item.getSoLuong())));
            sach.setSoLuongTon(Math.addExact(sach.getSoLuongTon(), item.getSoLuong()));
            sachRepository.save(sach);
            details.add(detail);
        }
        phieuNhap.setTongTien(total);
        PhieuNhap saved = repository.save(phieuNhap);
        for (ChiTietPhieuNhap detail : details) {
            detail.setPhieuNhap(saved);
        }
        saved.setChiTietPhieuNhaps(chiTietRepository.saveAll(details));
        return saved;
    }

    private void reverseStock(PhieuNhap phieuNhap) {
        for (ChiTietPhieuNhap detail : chiTietRepository.findAllByPhieuNhapId(phieuNhap.getId())) {
            Sach sach = detail.getSach();
            if (sach.getSoLuongTon() < detail.getSoLuong()) {
                throw new IllegalStateException("Không thể hủy phiếu: tồn kho đã thấp hơn số lượng nhập");
            }
            sach.setSoLuongTon(sach.getSoLuongTon() - detail.getSoLuong());
            sachRepository.save(sach);
        }
    }
}
