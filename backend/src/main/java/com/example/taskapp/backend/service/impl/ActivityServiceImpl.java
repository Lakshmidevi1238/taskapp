package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.entity.ActivityLog;
import com.example.taskapp.backend.entity.Board;
import com.example.taskapp.backend.entity.User;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.ActivityLogRepository;
import com.example.taskapp.backend.repository.BoardRepository;
import com.example.taskapp.backend.repository.UserRepository;
import com.example.taskapp.backend.service.ActivityService;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityLogRepository repo;
    private final BoardRepository boardRepo;
    private final UserRepository userRepo;

    public ActivityServiceImpl(
            ActivityLogRepository repo,
            BoardRepository boardRepo,
            UserRepository userRepo
    ) {
        this.repo = repo;
        this.boardRepo = boardRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void log(Long boardId,
                    Long userId,
                    String action,
                    String entityType,
                    Long entityId,
                    String json) {

        // 🔹 Validate input
        if (boardId == null) {
            throw new BadRequestException("Board id cannot be null");
        }

        if (action == null || action.isBlank()) {
            throw new BadRequestException("Action cannot be empty");
        }

        if (entityType == null || entityType.isBlank()) {
            throw new BadRequestException("Entity type cannot be empty");
        }

        // 🔹 Fetch board (must exist)
        Board board = boardRepo.findById(boardId)
                .orElseThrow(() ->
                        new NotFoundException("Board not found with id: " + boardId)
                );

        // 🔹 Fetch user (optional but must exist if provided)
        User user = null;
        if (userId != null) {
            user = userRepo.findById(userId)
                    .orElseThrow(() ->
                            new NotFoundException("User not found with id: " + userId)
                    );
        }

        // 🔹 Create activity log
        ActivityLog log = new ActivityLog();
        log.setBoard(board);
        log.setUser(user);
        log.setActionType(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetailsJson(json);

        repo.save(log);
    }
}
