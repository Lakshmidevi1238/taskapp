package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.entity.*;
import com.example.taskapp.backend.exception.BadRequestException;
import com.example.taskapp.backend.exception.ForbiddenException;
import com.example.taskapp.backend.exception.NotFoundException;
import com.example.taskapp.backend.repository.*;
import com.example.taskapp.backend.service.ActivityService;
import com.example.taskapp.backend.service.BoardService;
import com.example.taskapp.backend.service.RealtimeEventService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepo;
    private final UserRepository userRepo;
    private final BoardMemberRepository memberRepo;
    private final ActivityService activity;
    private final RealtimeEventService realtime;

    public BoardServiceImpl(
            BoardRepository boardRepo,
            UserRepository userRepo,
            BoardMemberRepository memberRepo,
            ActivityService activity,
            RealtimeEventService realtime
    ) {
        this.boardRepo = boardRepo;
        this.userRepo = userRepo;
        this.memberRepo = memberRepo;
        this.activity = activity;
        this.realtime = realtime;
    }

    // ================= CREATE =================

    @Override
    @Transactional
    public Board createBoard(String title, String description, Long ownerId) {

        if (title == null || title.isBlank()) {
            throw new BadRequestException("Board title cannot be empty");
        }

        User owner = userRepo.findById(ownerId)
                .orElseThrow(() ->
                        new NotFoundException("Owner not found with id: " + ownerId));

        Board b = new Board();
        b.setTitle(title);
        b.setDescription(description);
        b.setOwner(owner);
        b.setInviteCode(generateInviteCode());

        b = boardRepo.save(b);

        // add owner as ADMIN member
        BoardMember m = new BoardMember();
        m.setId(new BoardMemberId(b.getId(), ownerId));
        m.setBoard(b);
        m.setUser(owner);
        m.setRole("ADMIN");
        memberRepo.save(m);

        activity.log(
                b.getId(),
                ownerId,
                "BOARD_CREATED",
                "BOARD",
                b.getId(),
                null
        );

        realtime.publish(
                b.getId(),
                "BOARD_CREATED",
                Map.of("boardId", b.getId(), "title", b.getTitle())
        );

        return b;
    }

    // ================= GET BOARD =================

    @Override
    public Board getBoard(Long boardId, Long userId) {

        Board b = boardRepo.findById(boardId)
                .orElseThrow(() ->
                        new NotFoundException("Board not found with id: " + boardId));

        if (!hasAccess(boardId, userId, b)) {
            throw new ForbiddenException("Access denied to this board");
        }

        return b;
    }

    // ================= USER BOARDS =================

    @Override
    public List<Board> getUserBoards(Long userId) {

        User u = userRepo.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User not found with id: " + userId));

        List<Board> owned = boardRepo.findByOwner(u);
        List<BoardMember> memberships = memberRepo.findByUser_Id(userId);

        Set<Board> result = new LinkedHashSet<>(owned);

        for (BoardMember m : memberships) {
            result.add(m.getBoard());
        }

        return new ArrayList<>(result);
    }

    // ================= DELETE =================

    @Override
    @Transactional
    public void deleteBoard(Long boardId, Long userId) {

        Board b = boardRepo.findById(boardId)
                .orElseThrow(() ->
                        new NotFoundException("Board not found with id: " + boardId));

        if (!b.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only the owner can delete this board");
        }

        boardRepo.delete(b);

        activity.log(
                boardId,
                userId,
                "BOARD_DELETED",
                "BOARD",
                boardId,
                null
        );

        realtime.publish(
                boardId,
                "BOARD_DELETED",
                Map.of("boardId", boardId)
        );
    }

    // ================= JOIN =================

    @Override
    @Transactional
    public void joinByInviteCode(String code, Long userId) {

        if (code == null || code.isBlank()) {
            throw new BadRequestException("Invite code cannot be empty");
        }

        Board b = boardRepo.findByInviteCode(code)
                .orElseThrow(() ->
                        new NotFoundException("Invalid invite code"));

        if (memberRepo.existsByBoard_IdAndUser_Id(
                b.getId(), userId)) {
            return; // idempotent
        }

        User u = userRepo.findById(userId)
                .orElseThrow(() ->
                        new NotFoundException("User not found with id: " + userId));

        BoardMember m = new BoardMember();
        m.setId(new BoardMemberId(b.getId(), userId));
        m.setBoard(b);
        m.setUser(u);
        m.setRole("MEMBER");

        memberRepo.save(m);

        activity.log(
                b.getId(),
                userId,
                "MEMBER_JOINED",
                "BOARD",
                b.getId(),
                null
        );

        realtime.publish(
                b.getId(),
                "MEMBER_JOINED",
                Map.of("userId", userId)
        );
    }

    // ================= HELPERS =================

    private boolean hasAccess(Long boardId, Long userId, Board b) {

        if (b.getOwner().getId().equals(userId)) {
            return true;
        }

        return memberRepo.existsByBoard_IdAndUser_Id(boardId, userId);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString();
    }
    
    @Override
    public List<Map<String,Object>> getBoardMembers(Long boardId, Long userId) {

        Board board = boardRepo.findById(boardId)
                .orElseThrow(() ->
                        new NotFoundException("Board not found"));

        if (!hasAccess(boardId, userId, board)) {
            throw new ForbiddenException("Access denied");
        }

        List<BoardMember> members =
                memberRepo.findByBoard_Id(boardId);

        List<Map<String,Object>> result = new ArrayList<>();

        for (BoardMember m : members) {
            User u = m.getUser();

            result.add(Map.of(
                    "id", u.getId(),
                    "name", u.getName(),
                    "email", u.getEmail(),
                    "role", m.getRole()
            ));
        }

        return result;
    }

}
