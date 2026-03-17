package com.example.taskapp.backend.controller;

import com.example.taskapp.backend.dto.CreateBoardRequest;
import com.example.taskapp.backend.entity.Board;
import com.example.taskapp.backend.security.CustomUserDetails;
import com.example.taskapp.backend.service.BoardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateBoardRequest req) {

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Login required"));
        }

        Long userId = principal.getUser().getId();

        Board b = boardService.createBoard(
                req.getTitle(),
                req.getDescription(),
                userId
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(b);
    }

    // ================= MY BOARDS =================

    @GetMapping("/my")
    public ResponseEntity<?> myBoards(
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Login required"));
        }

        Long userId = principal.getUser().getId();

        List<Board> boards = boardService.getUserBoards(userId);

        return ResponseEntity.ok(boards);
    }

    // ================= GET ONE =================

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Login required"));
        }

        Long userId = principal.getUser().getId();

        Board b = boardService.getBoard(id, userId);

        return ResponseEntity.ok(b);
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Login required"));
        }

        Long userId = principal.getUser().getId();

        boardService.deleteBoard(id, userId);

        return ResponseEntity.ok(
                Map.of("message", "Board deleted"));
    }

    // ================= JOIN =================

    @PostMapping("/join/{code}")
    public ResponseEntity<?> join(
            @PathVariable String code,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Login required"));
        }

        Long userId = principal.getUser().getId();

        boardService.joinByInviteCode(code, userId);

        return ResponseEntity.ok(
                Map.of("message", "Joined board"));
    }
 // ================= MEMBERS =================

    @GetMapping("/{boardId}/members")
    public ResponseEntity<?> members(
            @PathVariable Long boardId,
            @AuthenticationPrincipal CustomUserDetails principal) {

        if (principal == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Login required"));
        }

        Long userId = principal.getUser().getId();

        return ResponseEntity.ok(
                boardService.getBoardMembers(boardId, userId)
        );
    }

}
