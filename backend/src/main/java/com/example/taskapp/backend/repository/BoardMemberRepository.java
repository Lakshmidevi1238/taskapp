package com.example.taskapp.backend.repository;

import com.example.taskapp.backend.entity.BoardMember;
import com.example.taskapp.backend.entity.BoardMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardMemberRepository
        extends JpaRepository<BoardMember, BoardMemberId> {

    List<BoardMember> findByBoard_Id(Long boardId);

    List<BoardMember> findByUser_Id(Long userId);

    boolean existsByBoard_IdAndUser_Id(Long boardId, Long userId);
}
