package com.example.taskapp.backend.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MoveTaskRequest {

	@NotNull
    private Long toListId;
	@Min(0)
    private int newPosition;

    public Long getToListId() { return toListId; }
    public void setToListId(Long toListId) { this.toListId = toListId; }

    public int getNewPosition() { return newPosition; }
    public void setNewPosition(int newPosition) { this.newPosition = newPosition; }
}
