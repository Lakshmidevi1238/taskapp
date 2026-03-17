package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.entity.PasswordResetToken;
import com.example.taskapp.backend.entity.User;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.PasswordResetTokenRepository;
import com.example.taskapp.backend.repository.UserRepository;
import com.example.taskapp.backend.service.PasswordResetService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final BCryptPasswordEncoder encoder;

    public PasswordResetServiceImpl(
            UserRepository userRepo,
            PasswordResetTokenRepository tokenRepo,
            BCryptPasswordEncoder encoder
    ) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
        this.encoder = encoder;
    }

    // ================= CREATE TOKEN =================

    @Override
    @Transactional
    public String createResetToken(String email) {

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email cannot be empty");
        }

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not found with email: " + email));

        // delete old tokens
        tokenRepo.deleteByUserId(user.getId());

        PasswordResetToken t = new PasswordResetToken();
        t.setUser(user);
        t.setToken(UUID.randomUUID().toString());
        t.setExpiryAt(LocalDateTime.now().plusMinutes(30));
        t.setUsed(false);

        tokenRepo.save(t);

        return t.getToken();
    }

    // ================= RESET PASSWORD =================

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        if (token == null || token.isBlank()) {
            throw new BadRequestException("Reset token is required");
        }

        if (newPassword == null || newPassword.isBlank()) {
            throw new BadRequestException("New password cannot be empty");
        }

        PasswordResetToken t = tokenRepo.findByToken(token)
                .orElseThrow(() ->
                        new NotFoundException("Invalid reset token"));

        if (t.isUsed()) {
            throw new BadRequestException("Token already used");
        }

        if (t.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Token expired");
        }

        User user = t.getUser();
        if (user == null) {
            throw new NotFoundException("User not found for this token");
        }

        user.setPasswordHash(encoder.encode(newPassword));
        userRepo.save(user);

        t.setUsed(true);
        tokenRepo.save(t);
    }
}
