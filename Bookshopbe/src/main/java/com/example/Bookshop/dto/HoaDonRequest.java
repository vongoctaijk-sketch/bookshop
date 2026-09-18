package com.example.Bookshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonRequest {

    @NotNull(message = "Người dùng không được để trống")
    private Integer nguoiDungId;

    @NotBlank(message = "Tên người nhận không được để trống")
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    private String sdtNguoiNhan;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String diaChiGiaoHang;

    @NotEmpty(message = "Hóa đơn phải có ít nhất một sách")
    @Valid
    private List<ChiTietHoaDonRequest> danhSachChiTiet;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietHoaDonRequest {

        @NotNull(message = "Sách không được để trống")
        private Integer sachId;

        @NotNull(message = "Số lượng không được để trống")
        private Integer soLuong;

        private BigDecimal donGia;
    }
}
