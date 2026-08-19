package com.example.Bookshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    @NotBlank
    private String password;
    private String email;
    private String hoTen;
    private String sdt;
    private String diaChi;
}
