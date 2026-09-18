package com.example.Bookshop.dto;

import java.util.List;

public record CurrentUserResponse(
        Integer id,
        String username,
        List<String> authorities
) {
}
