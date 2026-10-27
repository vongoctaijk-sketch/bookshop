package com.example.Bookshop.exception;

import com.example.Bookshop.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void convertsInvalidArgumentsToStandardBadRequestResponse() {
        ResponseEntity<ApiResponse<Void>> response =
                exceptionHandler.handleIllegalArgument(new IllegalArgumentException("Dữ liệu sai"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().success());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getBody().code());
        assertEquals("Dữ liệu sai", response.getBody().message());
    }
}
