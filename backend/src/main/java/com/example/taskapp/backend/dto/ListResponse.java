package com.example.taskapp.backend.dto;

import com.example.taskapp.backend.entity.ListEntity;

public class ListResponse {

    private Long id;
    private String title;
    private Integer position;

    public ListResponse(ListEntity l) {
        this.id = l.getId();
        this.title = l.getTitle();
        this.position = l.getPosition();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Integer getPosition() { return position; }
}
