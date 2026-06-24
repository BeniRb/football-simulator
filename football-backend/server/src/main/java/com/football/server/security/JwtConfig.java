package com.football.server.security;

import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;
import com.football.server.utils.Constants;

import javax.annotation.PostConstruct;

@Component
public class JwtConfig {
    private final Environment env;

    private String secret;
    private long accessExpiration;
    private long refreshExpiration;

    public JwtConfig(Environment env) {
        this.env = env;
    }

    @PostConstruct
    public void init() {
        this.secret = env.getProperty("JWT_SECRET", Constants.JWT_SECRET);
        this.accessExpiration = env.getProperty("JWT_ACCESS_EXPIRATION",
                Long.class, Constants.JWT_ACCESS_EXPIRATION);
        this.refreshExpiration = env.getProperty("JWT_REFRESH_EXPIRATION",
                Long.class, Constants.JWT_REFRESH_EXPIRATION);

        validate();
    }

    private void validate() {
        if (secret == null || secret.isBlank()) {
            throw new RuntimeException("JWT configuration failed: Secret key is missing.");
        }
        if (secret.length() < 32) {
            throw new RuntimeException("JWT configuration failed: Secret key must be at least 32 characters long.");
        }
        if (accessExpiration <= 0) {
            throw new RuntimeException("JWT configuration failed: Access expiration must be positive.");
        }
        if (refreshExpiration <= 0) {
            throw new RuntimeException("JWT configuration failed: Refresh expiration must be positive.");
        }
        if (refreshExpiration <= accessExpiration) {
            throw new RuntimeException("JWT configuration failed: Refresh expiration must be greater than access expiration.");
        }
    }

    public String getSecret() {
        return secret;
    }

    public long getAccessExpiration() {
        return accessExpiration;
    }

    public long getRefreshExpiration() {
        return refreshExpiration;
    }
}