package com.example.taskapp.backend.service.impl;

import com.example.taskapp.backend.service.RealtimeEventService;
import com.example.taskapp.backend.websocket.RealtimeEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RealtimeEventServiceImpl
        implements RealtimeEventService {

    private final SimpMessagingTemplate messaging;

    public RealtimeEventServiceImpl(
            SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    @Override
    public void publish(Long boardId,
                        String type,
                        Map<String,Object> payload) {

        RealtimeEvent event =
                new RealtimeEvent(type, boardId, payload);

        messaging.convertAndSend(
                "/topic/boards/" + boardId,
                event
        );
    }
}
