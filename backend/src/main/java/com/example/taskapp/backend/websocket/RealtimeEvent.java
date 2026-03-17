package com.example.taskapp.backend.websocket;

import java.time.LocalDateTime;
import java.util.Map;

public class RealtimeEvent {

    private int eventVersion = 1;
    private String type;
    private Long boardId;
    private Map<String, Object> payload;
    private LocalDateTime timestamp;

    public RealtimeEvent() {}

    public RealtimeEvent(String type,
                         Long boardId,
                         Map<String,Object> payload) {
        this.type = type;
        this.boardId = boardId;
        this.payload = payload;
        this.timestamp = LocalDateTime.now();
    }

    public int getEventVersion() { return eventVersion; }
    public String getType() { return type; }
    public Long getBoardId() { return boardId; }
    public Map<String,Object> getPayload() { return payload; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
