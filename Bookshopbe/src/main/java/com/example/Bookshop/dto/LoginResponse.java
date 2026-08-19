package com.example.Bookshop.dto;

import java.util.List;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String username,
        java.util.List<String> authorities
) {
}
