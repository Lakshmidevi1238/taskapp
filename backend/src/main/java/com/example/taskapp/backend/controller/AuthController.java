package com.example.taskapp.backend.controller;

import com.example.taskapp.backend.dto.*;
import com.example.taskapp.backend.service.AuthService;
import com.example.taskapp.backend.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(
            AuthService authService,
            PasswordResetService passwordResetService
    ) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest req) {

        AuthResponse res = authService.register(req);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(res);
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest req) {

        return ResponseEntity.ok(
                authService.login(req));
    }

    // ================= REFRESH =================

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest req) {

        return ResponseEntity.ok(
                authService.refresh(
                        req.getRefreshToken()));
    }

    // ================= FORGOT PASSWORD =================

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest req) {

        String token =
                passwordResetService
                        .createResetToken(req.getEmail());

        // assignment-mode → return token directly
        return ResponseEntity.ok(
                Map.of(
                    "message", "Reset token generated",
                    "resetToken", token
                )
        );
    }

    // ================= RESET PASSWORD =================

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest req) {

        passwordResetService.resetPassword(
                req.getToken(),
                req.getNewPassword()
        );

        return ResponseEntity.ok(
                Map.of("message", "Password updated"));
    }
}
