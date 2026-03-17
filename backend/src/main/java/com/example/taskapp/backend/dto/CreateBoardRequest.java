package com.example.taskapp.backend.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class CreateBoardRequest {

	
	@NotBlank(message = "Title is required")
    @Size(max = 150)
    private String title;
	@Size(max = 1000)
    private String description;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
