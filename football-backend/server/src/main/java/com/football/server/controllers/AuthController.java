package com.football.server.controllers;

import org.springframework.web.bind.annotation.*;
import com.football.server.database.AuthRepository;
import com.football.server.entities.User;
import com.football.server.requests.LoginRequest;
import com.football.server.responses.BasicResponse;
import com.football.server.responses.LoginResponse;
import com.football.server.service.AuthService;
import com.football.server.utils.CookieUtils;
import com.football.server.utils.Errors;
import com.football.server.utils.GenerateHash;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final AuthRepository authRepository;

    public AuthController(AuthService authService, AuthRepository authRepository) {
        this.authService = authService;
        this.authRepository = authRepository;
    }

    @PostMapping("/register")
    public LoginResponse register(@RequestParam String username, 
                                  @RequestParam String password, 
                                  @RequestParam String email) {

        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() || 
            email == null || email.trim().isEmpty()) {
            return new LoginResponse(Errors.ERROR_EMPTY_FIELD, false, "Missing required registration fields");
        }

        if (authRepository.findUserByUsername(username.trim()) != null) {
            return new LoginResponse(Errors.ERROR_USER_ALREADY_EXISTS, false, "Username already exists");
        }

        String securePasswordHash = GenerateHash.hashMd5(username.trim(), password.trim());

        User newUser = new User(username.trim(), securePasswordHash, email.trim());
        authRepository.saveUser(newUser);

        return new LoginResponse(null, true, "Registration successful");
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.loginUser(request.getPersonalId(), request.getPassword());

        if (loginResponse.isSuccess()) {
            CookieUtils.setAuthCookies(response,
                    loginResponse.getAccessToken(),
                    loginResponse.getRefreshToken());
            stripTokensFromBody(loginResponse);
        }
        return loginResponse;
    }

    @PostMapping("/logout")
    public LoginResponse logoutUser(
            @CookieValue(value = "accessToken", required = false) String token,
            HttpServletResponse response) {
        
        BasicResponse basicResponse = authService.logout(token);
        CookieUtils.clearAuthCookies(response);
        
        return new LoginResponse(basicResponse.getErrorCode(), basicResponse.isSuccess(), "Logout completed");
    }

    @PostMapping("/refresh")
    public LoginResponse refresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        
        String newAccessToken = authService.refresh(refreshToken);
        if (newAccessToken == null) {
            CookieUtils.clearAuthCookies(response);
            return new LoginResponse(Errors.ERROR_INVALID_TOKEN, false, "Token refresh failed");
        }
        
        CookieUtils.setAccessCookie(response, newAccessToken);
        return new LoginResponse(null, true, "Token refreshed successfully", newAccessToken, refreshToken);
    }

    private void stripTokensFromBody(LoginResponse loginResponse) {
        loginResponse.setAccessToken(null);
        loginResponse.setRefreshToken(null);
    }
}