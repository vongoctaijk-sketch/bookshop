package com.example.Bookshop.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Không tìm thấy tài nguyên");
    }
}
