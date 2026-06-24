package com.football.server.service;

import org.springframework.stereotype.Service;
import com.football.server.entities.User;
import com.football.server.responses.BasicResponse;
import com.football.server.responses.LoginResponse;
import com.football.server.utils.Errors;
import com.football.server.utils.GenerateHash;

@Service
public class AuthService {
    private final TokenService tokenService;
    private final UserService userService;

    public AuthService(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    public LoginResponse loginUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new LoginResponse(Errors.ERROR_EMPTY_FIELD, false, "Missing username or password");
        }
        
        username = username.trim();
        password = password.trim();
        
        String hashedPassword = GenerateHash.hashMd5(username, password);

        User user = userService.getUserByUsernameAndPassword(username, hashedPassword);
        if (user == null) {
            return new LoginResponse(Errors.ERROR_WRONG_CREDENTIALS, false, "Wrong username or password");
        }

        String refreshToken = tokenService.createRefreshToken(user);
        String accessToken = tokenService.createAccessToken(user);

        return new LoginResponse(null, true, "Login successful", accessToken, refreshToken);
    }

    public BasicResponse logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            return new BasicResponse(false, Errors.ERROR_INVALID_TOKEN);
        }
        Long userId = tokenService.getUserIdFromAccessToken(token);
        if (userId == null) {
            return new BasicResponse(false, Errors.ERROR_INVALID_TOKEN);
        }
        tokenService.logoutByUserId(userId);
        return new BasicResponse(true, null);
    }

    public String refresh(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return null;
        }
        return tokenService.refreshAccessToken(refreshToken);
    }
}