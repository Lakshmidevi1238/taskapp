package com.example.taskapp.backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.taskapp.backend.entity.Board;
import com.example.taskapp.backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByOwner(User owner);
    Optional<Board> findByInviteCode(String inviteCode);
}

