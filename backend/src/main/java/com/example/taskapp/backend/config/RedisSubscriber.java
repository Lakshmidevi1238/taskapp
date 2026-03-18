package com.example.taskapp.backend.config;

import com.example.taskapp.backend.websocket.RealtimeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public RedisSubscriber(SimpMessagingTemplate messagingTemplate,
                           ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = message.toString();

            RealtimeEvent event =
                    objectMapper.readValue(json, RealtimeEvent.class);

            // 🔥 Send to correct board topic
            messagingTemplate.convertAndSend(
                    "/topic/boards/" + event.getBoardId(),
                    event
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}