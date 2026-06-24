package com.football.server.entities;

import java.time.LocalDateTime;

public class RefreshToken {
    private Long id;
    private String token;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    
    // Change this from long to Long object wrapper
    private Long userId; 

    public RefreshToken() {
    }

    // Update constructor parameter type to Long
    public RefreshToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Long userId) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    // Update getter return type to Long
    public Long getUserId() {
        return userId;
    }

    // Update setter parameter type to Long
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}