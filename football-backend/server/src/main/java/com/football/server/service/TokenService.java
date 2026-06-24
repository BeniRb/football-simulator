package com.football.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.football.server.database.RefreshTokenRepository;
import com.football.server.database.AuthRepository;
import com.football.server.entities.RefreshToken;
import com.football.server.entities.User;
import com.football.server.security.JwtService;
import com.football.server.utils.Constants;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthRepository userRepository;
    private final JwtService jwtService;

    public TokenService(RefreshTokenRepository refreshTokenRepository,
                        AuthRepository authRepository,
                        JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = authRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public String createRefreshToken(User user) {
        String refreshTokenValue = jwtService.generateRefreshToken(user);
        LocalDateTime now = LocalDateTime.now();
        
        Date expDate = jwtService.extractExpiration(refreshTokenValue);
        LocalDateTime expiresAt = expDate != null 
                ? expDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                : now.plusHours(Constants.TOKEN_VALID_HOURS);

        RefreshToken existing = refreshTokenRepository.findByUserId(user.getId());
        if (existing != null) {
            existing.setToken(refreshTokenValue);
            existing.setCreatedAt(now);
            existing.setExpiresAt(expiresAt);
            refreshTokenRepository.save(existing);
        } else {
            RefreshToken fresh = new RefreshToken(refreshTokenValue, now, expiresAt, user.getId());
            refreshTokenRepository.save(fresh);
        }

        return refreshTokenValue;
    }

    public String createAccessToken(User user) {
        return jwtService.generateAccessToken(user);
    }

    public RefreshToken getValidRefreshToken(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.trim().isEmpty()) {
            return null;
        }
        if (!jwtService.isTokenValid(refreshTokenValue)) {
            deleteTokenIfExists(refreshTokenValue);
            return null;
        }
        if (!jwtService.isRefreshToken(refreshTokenValue)) {
            return null;
        }
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue);
        if (refreshToken == null || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            if (refreshToken != null) {
                refreshTokenRepository.delete(refreshToken);
            }
            return null;
        }
        return refreshToken;
    }

    public boolean isValidAccessToken(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            return false;
        }
        return jwtService.isTokenValid(accessToken) && jwtService.isAccessToken(accessToken);
    }

    public Long getUserIdFromAccessToken(String accessToken) {
        if (!isValidAccessToken(accessToken)) {
            return null;
        }
        // Safely extract the ID and handle Integer to Long type casting conversion
        Object idObj = jwtService.extractUserId(accessToken);
        if (idObj instanceof Integer) {
            return ((Integer) idObj).longValue();
        } else if (idObj instanceof Long) {
            return (Long) idObj;
        }
        return null;
    }

    public String refreshAccessToken(String refreshTokenValue) {
        RefreshToken refreshToken = getValidRefreshToken(refreshTokenValue);
        if (refreshToken == null) {
            return null;
        }
        User user = userRepository.findUserById(refreshToken.getUserId());
        if (user == null) {
            return null;
        }
        return createAccessToken(user);
    }

    public void logout(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.trim().isEmpty()) {
            return;
        }
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue);
        if (refreshToken != null) {
            refreshTokenRepository.delete(refreshToken);
        }
    }

    private void deleteTokenIfExists(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue);
        if (refreshToken != null) {
            refreshTokenRepository.delete(refreshToken);
        }
    }

    @Transactional
    public boolean logoutByUserId(Long userId) {
        if (userId == null) {
            return false;
        }
        refreshTokenRepository.deleteByUserId(userId);
        return true;
    }
}