package com.example.taskapp.backend.service;

public interface ActivityService {

    void log(
        Long boardId,
        Long userId,
        String action,
        String entityType,
        Long entityId,
        String json
    );
}
