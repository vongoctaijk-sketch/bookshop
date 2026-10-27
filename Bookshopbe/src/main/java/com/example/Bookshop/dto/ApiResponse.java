package com.example.Bookshop.dto;

public record ApiResponse<T>(
        boolean success,
        int code,
        String message,
        T data
) {

    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }

    public static <T> ApiResponse<T> failure(int code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
}
