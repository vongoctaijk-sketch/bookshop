package com.example.Bookshop.controller;

import com.example.Bookshop.dto.KhachHangDto;
import com.example.Bookshop.entity.HoaDon;
import com.example.Bookshop.entity.NguoiDung;
import com.example.Bookshop.repository.HoaDonRepository;
import com.example.Bookshop.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/khach-hang")
@RequiredArgsConstructor
public class KhachHangController {
    private final NguoiDungRepository nguoiDungRepository;
    private final HoaDonRepository hoaDonRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public List<KhachHangDto> getAll() {
        Map<Integer, List<HoaDon>> ordersByUser = hoaDonRepository.findAll().stream()
                .filter(order -> order.getNguoiDung() != null)
                .collect(Collectors.groupingBy(order -> order.getNguoiDung().getId()));
        return nguoiDungRepository.findAll().stream()
                .filter(user -> user.getNhomNguoiDung() != null
                        && "USER".equalsIgnoreCase(user.getNhomNguoiDung().getTenNhom()))
                .map(user -> toDto(user, ordersByUser.getOrDefault(user.getId(), List.of()))).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public KhachHangDto getById(@PathVariable Integer id) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        if (user.getNhomNguoiDung() == null || !"USER".equalsIgnoreCase(user.getNhomNguoiDung().getTenNhom())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }
        return toDto(user, hoaDonRepository.findAllByNguoiDungId(id));
    }

    @PutMapping("/{id}")
    @Transactional
    public KhachHangDto updateMembership(@PathVariable Integer id, @RequestBody KhachHangDto update) {
        NguoiDung user = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng"));
        if (user.getNhomNguoiDung() == null || !"USER".equalsIgnoreCase(user.getNhomNguoiDung().getTenNhom())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng");
        }
        if (update.getDiemTichLuy() != null) {
            if (update.getDiemTichLuy() < 0) throw new IllegalArgumentException("Điểm tích lũy không được âm");
            user.setDiemTichLuy(update.getDiemTichLuy());
            user.setHangThanhVien(update.getDiemTichLuy() >= 1000 ? "VIP"
                    : update.getDiemTichLuy() >= 500 ? "VANG"
                    : update.getDiemTichLuy() >= 100 ? "BAC" : "DONG");
            user.setVip(update.getDiemTichLuy() >= 1000);
            nguoiDungRepository.save(user);
        }
        return toDto(user, hoaDonRepository.findAllByNguoiDungId(id));
    }

    private KhachHangDto toDto(NguoiDung user, List<HoaDon> orders) {
        List<HoaDon> completed = orders.stream()
                .filter(order -> "COMPLETED".equalsIgnoreCase(order.getTrangThai())).toList();
        long totalSpent = completed.stream().map(HoaDon::getTongTien).filter(java.util.Objects::nonNull)
                .mapToLong(value -> value.longValue()).sum();
        long points = user.getDiemTichLuy() == null ? 0 : user.getDiemTichLuy();
        String tier = points >= 1000 ? "VIP" : points >= 500 ? "VANG" : points >= 100 ? "BAC" : "DONG";
        return new KhachHangDto(user.getId(), user.getHoTen(), user.getSdt(), user.getEmail(),
                orders.stream().map(HoaDon::getId).toList(), List.of(), points, tier,
                points >= 1000, totalSpent, completed.size());
    }
}
