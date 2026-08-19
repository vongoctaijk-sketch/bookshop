package com.example.Bookshop.service;

import com.example.Bookshop.dto.LoginRequest;
import com.example.Bookshop.dto.LoginResponse;
import com.example.Bookshop.dto.RegisterRequest;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(String refreshToken);
    RegisterRequest register(RegisterRequest request);
    void logout(String refreshToken);
}
