package com.example.taskapp.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.backend.entity.ListEntity;

import java.util.List;

public interface ListEntityRepository
        extends JpaRepository<ListEntity, Long> {

    List<ListEntity> findByBoardIdOrderByPositionAsc(Long boardId);
}
