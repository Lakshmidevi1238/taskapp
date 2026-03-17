package com.example.taskapp.backend.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class JwtPrincipalHandshakeHandler
        extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            org.springframework.web.socket.WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        String email = (String) attributes.get("userEmail");
        Long userId = (Long) attributes.get("userId");

        if (email == null) return null;

        return new WsUserPrincipal(email, userId);
    }
}
