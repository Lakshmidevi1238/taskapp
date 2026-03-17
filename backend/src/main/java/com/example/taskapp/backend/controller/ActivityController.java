package com.example.taskapp.backend.controller;

import com.example.taskapp.backend.entity.ActivityLog;
import com.example.taskapp.backend.repository.ActivityLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards/{boardId}/activity")
public class ActivityController {

    private final ActivityLogRepository repo;

    public ActivityController(ActivityLogRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public ResponseEntity<Page<ActivityLog>> getActivity(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {

        Page<ActivityLog> logs =
                repo.findByBoardIdOrderByCreatedAtDesc(
                        boardId,
                        PageRequest.of(page, size)
                );

        return ResponseEntity.ok(logs);
    }
}
