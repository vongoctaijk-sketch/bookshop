package com.example.Bookshop.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class PhieuNhapRequest {
    @NotNull
    private Integer nhaCungCapId;

    @NotNull
    private Integer nguoiDungId;

    @NotEmpty
    @Valid
    private List<ChiTietRequest> chiTietPhieuNhaps;

    @Getter
    @Setter
    public static class ChiTietRequest {
        @NotNull
        private Integer sachId;

        @NotNull
        @Positive
        private Integer soLuong;

        @NotNull
        @PositiveOrZero
        private BigDecimal giaNhap;
    }
}
