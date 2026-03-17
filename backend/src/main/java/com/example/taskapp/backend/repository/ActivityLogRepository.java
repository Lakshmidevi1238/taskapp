package com.example.taskapp.backend.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.backend.entity.ActivityLog;

public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByBoardIdOrderByCreatedAtDesc(
            Long boardId,
            Pageable pageable
    );
}

