package com.example.taskapp.backend.service;

import com.example.taskapp.backend.entity.User;
import com.example.taskapp.backend.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken create(User user);

    RefreshToken verify(String token);

    void deleteForUser(Long userId);
}
