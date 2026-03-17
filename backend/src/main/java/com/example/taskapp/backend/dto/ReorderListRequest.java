package com.example.taskapp.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class ReorderListRequest {

    @NotEmpty(message = "orderedIds cannot be empty")
    private List<Long> orderedIds;

    public List<Long> getOrderedIds() {
        return orderedIds;
    }

    public void setOrderedIds(List<Long> orderedIds) {
        this.orderedIds = orderedIds;
    }
}
