package com.example.taskapp.backend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TaskAssigneeId implements Serializable {

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "user_id")
    private Long userId;

    public TaskAssigneeId() {}

    public TaskAssigneeId(Long taskId, Long userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public Long getTaskId() { return taskId; }
    public Long getUserId() { return userId; }

    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public void setUserId(Long userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaskAssigneeId)) return false;
        TaskAssigneeId that = (TaskAssigneeId) o;
        return Objects.equals(taskId, that.taskId)
            && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, userId);
    }
}
