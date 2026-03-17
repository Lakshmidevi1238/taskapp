package com.example.taskapp.backend.controller;

import com.example.taskapp.backend.dto.CreateListRequest;
import com.example.taskapp.backend.dto.ListResponse;
import com.example.taskapp.backend.dto.ReorderListRequest;
import com.example.taskapp.backend.dto.UpdateListRequest;
import com.example.taskapp.backend.entity.ListEntity;
import com.example.taskapp.backend.security.CustomUserDetails;
import com.example.taskapp.backend.service.ListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    // ================= CREATE =================

    @PostMapping("/boards/{boardId}/lists")
    public ResponseEntity<ListResponse> createList(
            @PathVariable Long boardId,
            @Valid @RequestBody CreateListRequest req) {

        ListEntity list =
                listService.createList(boardId, req.getTitle());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ListResponse(list));
    }

    // ================= GET =================

    @GetMapping("/boards/{boardId}/lists")
    public ResponseEntity<List<ListResponse>> getLists(
            @PathVariable Long boardId) {

        List<ListResponse> response =
                listService.getBoardLists(boardId)
                        .stream()
                        .map(ListResponse::new)
                        .toList();

        return ResponseEntity.ok(response);
    }

    // ================= DELETE =================

    @DeleteMapping("/lists/{listId}")
    public ResponseEntity<Map<String,String>> deleteList(
            @PathVariable Long listId) {

        listService.deleteList(listId);

        return ResponseEntity.ok(
                Map.of("message", "List deleted"));
    }

    // ================= REORDER =================

    @PutMapping("/boards/{boardId}/lists/reorder")
    public ResponseEntity<Map<String,String>> reorderLists(
            @PathVariable Long boardId,
            @Valid @RequestBody ReorderListRequest req) {

        listService.reorderLists(
                boardId,
                req.getOrderedIds()
        );

        return ResponseEntity.ok(
                Map.of("message", "Lists reordered"));
    }

  

 // ================= RENAME =================

    @PutMapping("/lists/{listId}")
    public ResponseEntity<ListResponse> rename(
            @PathVariable Long listId,
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody UpdateListRequest req) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        Long userId = principal.getUser().getId();

        ListEntity updated =
                listService.renameList(
                        listId,
                        req.getTitle(),
                        userId
                );

        return ResponseEntity.ok(
                new ListResponse(updated)
        );
    }

}
