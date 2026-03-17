package com.example.taskapp.backend.service;

import java.util.Map;

public interface RealtimeEventService {

    void publish(Long boardId,
                 String type,
                 Map<String,Object> payload);
}
