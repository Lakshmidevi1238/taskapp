package com.example.taskapp.backend.controller;

import com.example.taskapp.backend.dto.*;
import com.example.taskapp.backend.entity.Task;
import com.example.taskapp.backend.security.CustomUserDetails;
import com.example.taskapp.backend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ================= CREATE =================

    @PostMapping("/lists/{listId}/tasks")
    public ResponseEntity<Task> createTask(
            @PathVariable Long listId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateTaskRequest req) {

        Long userId = principal.getUser().getId();

        Task t = taskService.createTask(
                listId,
                req.getTitle(),
                req.getDescription(),
                userId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(t);
    }

    // ================= UPDATE =================

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Task> updateTask(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest req) {

        return ResponseEntity.ok(
                taskService.updateTask(
                        taskId,
                        req.getTitle(),
                        req.getDescription()
                )
        );
    }

    // ================= DELETE =================

    @DeleteMapping("/tasks/{taskId}")
    public ResponseEntity<Map<String,String>> deleteTask(
            @PathVariable Long taskId) {

        taskService.deleteTask(taskId);

        return ResponseEntity.ok(
                Map.of("message", "Task deleted"));
    }

    // ================= MOVE =================

    @PutMapping("/tasks/{taskId}/move")
    public ResponseEntity<Task> moveTask(
            @PathVariable Long taskId,
            @Valid @RequestBody MoveTaskRequest req) {

        return ResponseEntity.ok(
                taskService.moveTask(
                        taskId,
                        req.getToListId(),
                        req.getNewPosition()
                )
        );
    }

    // ================= SEARCH + PAGINATION =================

    @GetMapping("/boards/{boardId}/tasks/search")
    public ResponseEntity<Page<TaskResponse>> search(
            @PathVariable Long boardId,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Task> result =
                taskService.search(boardId, q, page, size);

        Page<TaskResponse> mapped =
                result.map(TaskResponse::new);

        return ResponseEntity.ok(mapped);
    }

}
