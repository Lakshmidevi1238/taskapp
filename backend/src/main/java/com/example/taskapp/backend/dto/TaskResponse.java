package com.example.taskapp.backend.dto;

import com.example.taskapp.backend.entity.Task;
import com.example.taskapp.backend.entity.TaskAssignee;

import java.util.List;
import java.util.Map;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Long listId;
    private Long boardId;
    private int position;

    private List<Map<String,Object>> assignees;

    public TaskResponse(Task t) {
        this.id = t.getId();
        this.title = t.getTitle();
        this.description = t.getDescription();
        this.listId = t.getList().getId();
        this.boardId = t.getBoard().getId();
        this.position = t.getPosition();

        this.assignees = t.getAssignees()
                .stream()
                .map(a -> {
                    Map<String,Object> m = new java.util.HashMap<>();
                    m.put("id", a.getUser().getId());
                    m.put("name", a.getUser().getName());
                    return m;
                })
                .toList();

    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Long getListId() { return listId; }
    public Long getBoardId() { return boardId; }
    public int getPosition() { return position; }
    public List<Map<String,Object>> getAssignees() { return assignees; }
}
