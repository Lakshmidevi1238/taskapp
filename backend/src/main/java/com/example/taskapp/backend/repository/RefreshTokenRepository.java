package com.example.taskapp.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.backend.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(Long userId);
}

