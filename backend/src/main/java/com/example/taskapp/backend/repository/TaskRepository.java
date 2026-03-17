package com.example.taskapp.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.backend.entity.Task;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByList_IdOrderByPositionAsc(Long listId);

    Page<Task> findByBoard_Id(Long boardId, Pageable pageable);

    Page<Task> findByBoard_IdAndTitleContainingIgnoreCase(
            Long boardId,
            String query,
            Pageable pageable
    );
}

