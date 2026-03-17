package com.example.taskapp.backend.dto;

import jakarta.validation.constraints.NotNull;

public class AssignRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
