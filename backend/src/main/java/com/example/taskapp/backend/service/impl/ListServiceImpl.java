package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.entity.Board;
import com.example.taskapp.backend.entity.BoardMemberId;
import com.example.taskapp.backend.entity.ListEntity;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.BoardMemberRepository;
import com.example.taskapp.backend.repository.BoardRepository;
import com.example.taskapp.backend.repository.ListEntityRepository;
import com.example.taskapp.backend.service.ActivityService;
import com.example.taskapp.backend.service.ListService;
import com.example.taskapp.backend.service.RealtimeEventService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ListServiceImpl implements ListService {

    private final ListEntityRepository listRepo;
    private final BoardRepository boardRepo;
    private final BoardMemberRepository boardMemberRepo;   // ✅ ADDED
    private final ActivityService activity;
    private final RealtimeEventService realtime;

    public ListServiceImpl(
            ListEntityRepository listRepo,
            BoardRepository boardRepo,
            BoardMemberRepository boardMemberRepo,   // ✅ ADDED
            ActivityService activity,
            RealtimeEventService realtime
    ) {
        this.listRepo = listRepo;
        this.boardRepo = boardRepo;
        this.boardMemberRepo = boardMemberRepo;     // ✅ ADDED
        this.activity = activity;
        this.realtime = realtime;
    }

    // ================= CREATE =================

    @Override
    @Transactional
    public ListEntity createList(Long boardId, String title) {

        if (boardId == null) {
            throw new BadRequestException("Board id cannot be null");
        }

        if (title == null || title.isBlank()) {
            throw new BadRequestException("List title cannot be empty");
        }

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() ->
                        new NotFoundException("Board not found with id: " + boardId));

        List<ListEntity> existing =
                listRepo.findByBoardIdOrderByPositionAsc(boardId);

        int nextPos = existing.size();

        ListEntity l = new ListEntity();
        l.setBoard(board);
        l.setTitle(title);
        l.setPosition(nextPos);

        l = listRepo.save(l);

        activity.log(
                boardId,
                null,
                "LIST_CREATED",
                "LIST",
                l.getId(),
                null
        );

        realtime.publish(
                boardId,
                "LIST_CREATED",
                Map.of(
                        "listId", l.getId(),
                        "title", l.getTitle(),
                        "position", l.getPosition()
                )
        );

        return l;
    }

    // ================= DELETE =================

    @Override
    @Transactional
    public void deleteList(Long listId) {

        if (listId == null) {
            throw new BadRequestException("List id cannot be null");
        }

        ListEntity l = listRepo.findById(listId)
                .orElseThrow(() ->
                        new NotFoundException("List not found with id: " + listId));

        Long boardId = l.getBoard().getId();

        listRepo.delete(l);

        activity.log(
                boardId,
                null,
                "LIST_DELETED",
                "LIST",
                listId,
                null
        );

        realtime.publish(
                boardId,
                "LIST_DELETED",
                Map.of("listId", listId)
        );
    }

    // ================= GET =================

    @Override
    public List<ListEntity> getBoardLists(Long boardId) {

        if (boardId == null) {
            throw new BadRequestException("Board id cannot be null");
        }

        return listRepo.findByBoardIdOrderByPositionAsc(boardId);
    }

    // ================= REORDER =================

    @Override
    @Transactional
    public void reorderLists(Long boardId, List<Long> orderedIds) {

        if (boardId == null || orderedIds == null || orderedIds.isEmpty()) {
            throw new BadRequestException("Invalid reorder payload");
        }

        List<ListEntity> lists =
                listRepo.findByBoardIdOrderByPositionAsc(boardId);

        if (lists.size() != orderedIds.size()) {
            throw new BadRequestException("List count mismatch during reorder");
        }

        Map<Long, ListEntity> map = new HashMap<>();
        for (ListEntity l : lists) {
            map.put(l.getId(), l);
        }

        int pos = 0;

        for (Long id : orderedIds) {

            ListEntity l = map.get(id);

            if (l == null) {
                throw new BadRequestException("List ID mismatch during reorder");
            }

            l.setPosition(pos++);
        }

        listRepo.saveAll(lists);
        activity.log(
        	    boardId,
        	    null,
        	    "LIST_REORDERED",
        	    "LIST",
        	    null,
        	    null
        	);

        realtime.publish(
                boardId,
                "LIST_REORDERED",
                Map.of("orderedIds", orderedIds)
        );
    }

    // ================= RENAME =================

    @Override
    @Transactional
    public ListEntity renameList(Long listId, String title, Long userId) {

        if (listId == null) {
            throw new BadRequestException("List id cannot be null");
        }

        if (title == null || title.isBlank()) {
            throw new BadRequestException("Title cannot be empty");
        }

        ListEntity l = listRepo.findById(listId)
                .orElseThrow(() ->
                        new NotFoundException("List not found"));

        Long boardId = l.getBoard().getId();

        // 🔐 SECURITY CHECK (Correct Way)
        boolean member = boardMemberRepo
                .existsById(new BoardMemberId(boardId, userId));

        if (!member) {
            throw new BadRequestException("You are not a member of this board");
        }

        l.setTitle(title);

        l = listRepo.save(l);

        activity.log(
                boardId,
                userId,
                "LIST_RENAMED",
                "LIST",
                listId,
                null
        );

        realtime.publish(
                boardId,
                "LIST_UPDATED",
                Map.of(
                        "listId", listId,
                        "title", title
                )
        );

        return l;
    }
}
