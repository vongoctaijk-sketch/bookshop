package com.example.Bookshop.configs;

import com.example.Bookshop.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return JacksonJsonHttpMessageConverter.class.isAssignableFrom(converterType)
                || MappingJackson2HttpMessageConverter.class.isAssignableFrom(converterType);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (body == null
                || body instanceof ApiResponse<?>
                || !request.getURI().getPath().startsWith("/api/")) {
            return body;
        }

        int statusCode = response instanceof ServletServerHttpResponse servletResponse
                ? servletResponse.getServletResponse().getStatus()
                : HttpStatus.OK.value();
        String message = statusCode >= 200 && statusCode < 300 ? "Success" : "Request failed";
        return ApiResponse.success(statusCode, message, body);
    }
}
