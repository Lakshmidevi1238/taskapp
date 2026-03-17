package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.entity.RefreshToken;
import com.example.taskapp.backend.entity.User;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.RefreshTokenRepository;
import com.example.taskapp.backend.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final long refreshMs;

    public RefreshTokenServiceImpl(
            RefreshTokenRepository repo,
            @Value("${jwt.refresh-expiration-ms}") long refreshMs
    ) {
        this.repo = repo;
        this.refreshMs = refreshMs;
    }

    // ================= CREATE =================

    @Override
    @Transactional
    public RefreshToken create(User user) {

        if (user == null) {
            throw new BadRequestException("User cannot be null for refresh token creation");
        }

        RefreshToken rt = new RefreshToken();
        rt.setToken(UUID.randomUUID().toString());
        rt.setUser(user);

        // refreshMs is already in milliseconds
        rt.setExpiryDate(
                LocalDateTime.now().plusNanos(refreshMs * 1_000_000)
        );

        return repo.save(rt);
    }

    // ================= VERIFY =================

    @Override
    @Transactional
    public RefreshToken verify(String token) {

        if (token == null || token.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() ->
                        new NotFoundException("Invalid refresh token"));

        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            repo.delete(rt);
            throw new BadRequestException("Refresh token expired");
        }

        return rt;
    }

    // ================= DELETE =================

    @Override
    @Transactional
    public void deleteForUser(Long userId) {

        if (userId == null) {
            throw new BadRequestException("User id cannot be null");
        }

        repo.deleteByUserId(userId);
    }
}
