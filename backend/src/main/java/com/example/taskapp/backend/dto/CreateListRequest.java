package com.example.taskapp.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateListRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150)
    private String title;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
