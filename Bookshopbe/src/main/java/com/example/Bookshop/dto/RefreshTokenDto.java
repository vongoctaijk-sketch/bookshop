package com.example.Bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto {

    private Long id;
    private String token;
    private String username;
    private LocalDateTime expiryDate;
    private Boolean revoked;
}
