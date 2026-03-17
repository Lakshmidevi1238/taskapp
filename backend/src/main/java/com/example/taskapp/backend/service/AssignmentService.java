package com.example.taskapp.backend.service;

public interface AssignmentService {

    void assign(Long taskId, Long userId);

    void unassign(Long taskId, Long userId);
}
