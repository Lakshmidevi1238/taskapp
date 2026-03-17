package com.example.taskapp.backend.service;

import com.example.taskapp.backend.entity.Board;

import java.util.List;
import java.util.Map;

public interface BoardService {

    Board createBoard(String title, String description, Long ownerId);

    Board getBoard(Long boardId, Long userId);

    List<Board> getUserBoards(Long userId);

    void deleteBoard(Long boardId, Long userId);

    void joinByInviteCode(String code, Long userId);
    List<Map<String,Object>> getBoardMembers(Long boardId, Long userId);

}
