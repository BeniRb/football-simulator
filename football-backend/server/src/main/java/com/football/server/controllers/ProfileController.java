package com.football.server.controllers;

import org.springframework.web.bind.annotation.*;
import com.football.server.responses.ProfileResponse;
import com.football.server.service.ProfileService;
import com.football.server.service.TokenService;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/user")
public class ProfileController {

    private final TokenService tokenService;
    private final ProfileService profileService;

    public ProfileController(TokenService tokenService, ProfileService profileService) {
        this.tokenService = tokenService;
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public ProfileResponse getUserProfile(
            @CookieValue(value = "accessToken", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        Long userId = tokenService.getUserIdFromAccessToken(token);
        if (userId == null) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        return profileService.getProfileData(userId, token);
    }

    @PostMapping("/wallet/deposit")
    public ProfileResponse depositFunds(
            @CookieValue(value = "accessToken", required = false) String token,
            @RequestParam int amount) {

        if (token == null || token.isEmpty() || amount <= 0) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        Long userId = tokenService.getUserIdFromAccessToken(token);
        if (userId == null) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        return profileService.deposit(userId, token, amount);
    }

    @PostMapping("/wallet/withdraw")
    public ProfileResponse withdrawFunds(
            @CookieValue(value = "accessToken", required = false) String token,
            @RequestParam int amount) {

        if (token == null || token.isEmpty() || amount <= 0) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        Long userId = tokenService.getUserIdFromAccessToken(token);
        if (userId == null) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        return profileService.withdraw(userId, token, amount);
    }
}