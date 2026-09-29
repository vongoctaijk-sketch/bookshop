package com.example.Bookshop.entity;

import java.time.LocalDateTime;

public record SachEmbedding(
        Integer sachId,
        String content,
        String vector,
        LocalDateTime updatedAt
) {
}
