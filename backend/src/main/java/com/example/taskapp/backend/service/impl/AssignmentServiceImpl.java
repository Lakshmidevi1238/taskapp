package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.entity.*;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.*;
import com.example.taskapp.backend.service.ActivityService;
import com.example.taskapp.backend.service.AssignmentService;
import com.example.taskapp.backend.service.RealtimeEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    private final TaskRepository taskRepo;
    private final UserRepository userRepo;
    private final TaskAssigneeRepository assigneeRepo;
    private final ActivityService activity;
    private final RealtimeEventService realtime;

    public AssignmentServiceImpl(
            TaskRepository taskRepo,
            UserRepository userRepo,
            TaskAssigneeRepository assigneeRepo,
            ActivityService activity,
            RealtimeEventService realtime
    ) {
        this.taskRepo = taskRepo;
        this.userRepo = userRepo;
        this.assigneeRepo = assigneeRepo;
        this.activity = activity;
        this.realtime = realtime;
    }

    // ================= ASSIGN =================

    @Override
    @Transactional
    public void assign(Long taskId, Long userId) {

        if (taskId == null || userId == null) {
            throw new BadRequestException("Task id and User id cannot be null");
        }

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException("Task not found with id: " + taskId));

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User not found with id: " + userId));

        TaskAssigneeId id = new TaskAssigneeId(taskId, userId);

        if (assigneeRepo.existsById(id)) {
            return; // idempotent (no error, safe repeat call)
        }

        TaskAssignee ta = new TaskAssignee();
        ta.setId(id);
        ta.setTask(task);
        ta.setUser(user);

        assigneeRepo.save(ta);

        Long boardId = task.getBoard().getId();

        activity.log(
                boardId,
                userId,
                "TASK_ASSIGNED",
                "TASK",
                taskId,
                null
        );

        realtime.publish(
                boardId,
                "TASK_ASSIGNED",
                Map.of(
                        "taskId", taskId,
                        "userId", userId
                )
        );
    }

    // ================= UNASSIGN =================

    @Override
    @Transactional
    public void unassign(Long taskId, Long userId) {

        if (taskId == null || userId == null) {
            throw new BadRequestException("Task id and User id cannot be null");
        }

        Task task = taskRepo.findById(taskId)
                .orElseThrow(() ->
                        new NotFoundException("Task not found with id: " + taskId));

        TaskAssigneeId id = new TaskAssigneeId(taskId, userId);

        if (!assigneeRepo.existsById(id)) {
            return; // idempotent
        }

        assigneeRepo.deleteById(id);

        Long boardId = task.getBoard().getId();

        activity.log(
                boardId,
                userId,
                "TASK_UNASSIGNED",
                "TASK",
                taskId,
                null
        );

        realtime.publish(
                boardId,
                "TASK_UNASSIGNED",
                Map.of(
                        "taskId", taskId,
                        "userId", userId
                )
        );
    }
}
