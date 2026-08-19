package com.example.Bookshop.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshAccessTokenRequest(
        @NotBlank String refreshToken
) {
}
