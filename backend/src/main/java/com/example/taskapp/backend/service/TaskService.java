package com.example.taskapp.backend.service;

import com.example.taskapp.backend.entity.Task;
import org.springframework.data.domain.Page;

public interface TaskService {

    Task createTask(Long listId, String title, String desc, Long userId);

    Task updateTask(Long taskId, String title, String desc);

    void deleteTask(Long taskId);

    Task moveTask(Long taskId, Long toListId, int newPosition);

    Page<Task> search(Long boardId, String query, int page, int size);
}
