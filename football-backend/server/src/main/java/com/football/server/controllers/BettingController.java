package com.football.server.controllers;

import com.football.server.entities.Bet;
import com.football.server.requests.BetDto;
import com.football.server.responses.BettingResponse;
import com.football.server.service.BettingService;
import com.football.server.service.TokenService;
import com.football.server.utils.Errors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/bets")
public class BettingController {

    private final BettingService bettingService;
    private final TokenService tokenService; // Inject TokenService

    public BettingController(BettingService bettingService, TokenService tokenService) {
        this.bettingService = bettingService;
        this.tokenService = tokenService;
    }

    @PostMapping("/place")
    public ResponseEntity<BettingResponse<Void>> placeBet(HttpServletRequest request, @RequestBody BetDto betDto) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.ok(
                    new BettingResponse<>(Errors.ERROR_INVALID_TOKEN, false, "Unauthorized: Missing session token.")
            );
        }

        BettingResponse<Void> serviceResponse = bettingService.placeBet(
                token,
                betDto.getMatchId(),
                betDto.getPrediction(),
                betDto.getWagerAmount(),
                betDto.getClientOdds()
        );

        return ResponseEntity.ok(serviceResponse);
    }

    @GetMapping("/history")
    public ResponseEntity<BettingResponse<List<Bet>>> getBettingHistory(HttpServletRequest request) {
        String token = extractToken(request);
        if (token == null) {
            return ResponseEntity.ok(
                    new BettingResponse<>(Errors.ERROR_INVALID_TOKEN, false, "Unauthorized: Missing session token.", Collections.emptyList())
            );
        }

        // Extract the user ID from the access token
        Long userId = tokenService.getUserIdFromAccessToken(token);
        if (userId == null) {
            return ResponseEntity.ok(
                    new BettingResponse<>(Errors.ERROR_INVALID_TOKEN, false, "Unauthorized: Invalid session token.", Collections.emptyList())
            );
        }

        // Pass the extracted Long userId instead of the token string
        List<Bet> history = bettingService.getUserBettingHistory(userId);
        return ResponseEntity.ok(new BettingResponse<>(null, true, "Betting history retrieved successfully.", history));
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // Modified to extract the accessToken since history queries depend on the active access token context
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}