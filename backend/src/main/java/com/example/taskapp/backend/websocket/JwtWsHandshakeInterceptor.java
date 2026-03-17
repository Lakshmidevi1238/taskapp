package com.example.taskapp.backend.websocket;

import com.example.taskapp.backend.security.JwtUtil;
import org.springframework.http.server.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class JwtWsHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public JwtWsHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String query = request.getURI().getQuery();
        if (query == null || !query.contains("token=")) {
            return false;
        }

        String token = query.split("token=")[1];

        if (!jwtUtil.isValid(token)) {
            return false;
        }

        String email = jwtUtil.extractEmail(token);
        Long userId = jwtUtil.extractUserId(token);

        attributes.put("userEmail", email);
        attributes.put("userId", userId);

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
    }
}
