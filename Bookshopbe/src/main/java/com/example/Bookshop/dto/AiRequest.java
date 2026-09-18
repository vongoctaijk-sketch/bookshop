package com.example.Bookshop.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;

public record AiRequest(
        @NotBlank(message = "Prompt không được để trống")
        @JsonAlias({"message", "content"})
        String prompt
) {
}
