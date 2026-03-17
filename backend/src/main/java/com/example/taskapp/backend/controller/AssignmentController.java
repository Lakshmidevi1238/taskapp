package com.example.taskapp.backend.controller;

import com.example.taskapp.backend.dto.AssignRequest;
import com.example.taskapp.backend.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    // ================= ASSIGN =================

    @PostMapping("/{taskId}/assignees")
    public ResponseEntity<Map<String, String>> assign(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignRequest req) {

        assignmentService.assign(taskId, req.getUserId());

        return ResponseEntity.ok(
                Map.of("message", "User assigned"));
    }

    // ================= UNASSIGN =================

    @DeleteMapping("/{taskId}/assignees")
    public ResponseEntity<Map<String, String>> unassign(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignRequest req) {

        assignmentService.unassign(taskId, req.getUserId());

        return ResponseEntity.ok(
                Map.of("message", "User unassigned"));
    }
}
