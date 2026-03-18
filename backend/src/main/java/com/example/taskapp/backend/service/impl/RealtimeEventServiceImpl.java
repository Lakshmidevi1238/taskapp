package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.service.RealtimeEventService;
import com.example.taskapp.backend.websocket.RealtimeEvent;
import com.example.taskapp.backend.config.RedisPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RealtimeEventServiceImpl implements RealtimeEventService {

    private final RedisPublisher publisher;

    public RealtimeEventServiceImpl(RedisPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void publish(Long boardId,
                        String type,
                        Map<String, Object> payload) {

        RealtimeEvent event =
                new RealtimeEvent(type, boardId, payload);

        // 🔥 Send to Redis instead of WebSocket directly
        publisher.publish(event);
    }
}