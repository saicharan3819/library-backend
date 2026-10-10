package com.library.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps login tokens in memory: token -> userId. (Tokens reset when the server restarts.) */
@Component
public class TokenStore {
    private final Map<String, Long> tokens = new ConcurrentHashMap<>();

    public String create(Long userId) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, userId);
        return token;
    }

    public Long resolve(String token) {
        return tokens.get(token);
    }
}
