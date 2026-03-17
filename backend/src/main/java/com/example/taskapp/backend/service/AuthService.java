package com.example.taskapp.backend.service;

import com.example.taskapp.backend.dto.*;

public interface AuthService {

    AuthResponse register(RegisterRequest req);

    AuthResponse login(LoginRequest req);

    AuthResponse refresh(String refreshToken);
}
