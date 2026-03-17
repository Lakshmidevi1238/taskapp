package com.example.taskapp.backend.websocket;

import java.security.Principal;

public class WsUserPrincipal implements Principal {

    private final String name;
    private final Long userId;

    public WsUserPrincipal(String name, Long userId) {
        this.name = name;
        this.userId = userId;
    }

    @Override
    public String getName() {
        return name;
    }

    public Long getUserId() {
        return userId;
    }
}
