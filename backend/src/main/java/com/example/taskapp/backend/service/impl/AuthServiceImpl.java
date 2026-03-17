package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.dto.*;
import com.example.taskapp.backend.entity.RefreshToken;
import com.example.taskapp.backend.entity.User;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.ConflictException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.UserRepository;
import com.example.taskapp.backend.security.JwtUtil;
import com.example.taskapp.backend.service.AuthService;
import com.example.taskapp.backend.service.RefreshTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshService;

    public AuthServiceImpl(
            UserRepository userRepo,
            BCryptPasswordEncoder encoder,
            JwtUtil jwtUtil,
            RefreshTokenService refreshService
    ) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.refreshService = refreshService;
    }

    // ================= REGISTER =================

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        if (userRepo.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User u = new User();
        u.setName(req.getName());
        u.setEmail(req.getEmail());
        u.setPasswordHash(
                encoder.encode(req.getPassword()));
        u.setRole("USER");

        u = userRepo.save(u);

        
        String access =
                jwtUtil.generateToken(u.getId(), u.getEmail());

        RefreshToken rt = refreshService.create(u);

        return new AuthResponse(
                access,
                rt.getToken(),
                u.getId(),
                u.getEmail(),
                u.getName()
        );
    }

    // ================= LOGIN =================

    @Override
    @Transactional
    public AuthResponse login(LoginRequest req) {

        User u = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() ->
                        new NotFoundException("Invalid credentials"));

        if (!encoder.matches(
                req.getPassword(),
                u.getPasswordHash())) {

            throw new NotFoundException("Invalid credentials");
        }

        
        String access =
                jwtUtil.generateToken(u.getId(), u.getEmail());

        // rotate refresh token
        refreshService.deleteForUser(u.getId());
        RefreshToken rt = refreshService.create(u);

        return new AuthResponse(
                access,
                rt.getToken(),
                u.getId(),
                u.getEmail(),
                u.getName()
        );
    }

    // ================= REFRESH =================

    @Override
    @Transactional
    public AuthResponse refresh(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        RefreshToken rt = refreshService.verify(refreshToken);

        User u = rt.getUser();
        if (u == null) {
            throw new NotFoundException("User not found");
        }

        String access =
                jwtUtil.generateToken(u.getId(), u.getEmail());

        // rotate refresh token
        refreshService.deleteForUser(u.getId());
        RefreshToken newRt = refreshService.create(u);

        return new AuthResponse(
                access,
                newRt.getToken(),
                u.getId(),
                u.getEmail(),
                u.getName()
        );
    }
}
