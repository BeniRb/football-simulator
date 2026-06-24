package com.football.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.football.server.responses.MatchSimulationResponse;
import com.football.server.service.MatchSimulationService;
import com.football.server.service.LeaderboardService;
import com.football.server.requests.LeaderboardDto;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MatchSimulationService matchSimulationService;
    private final LeaderboardService leaderboardService;

    public AdminController(MatchSimulationService matchSimulationService, LeaderboardService leaderboardService) {
        this.matchSimulationService = matchSimulationService;
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<MatchSimulationResponse<List<LeaderboardDto>>> getUserLeaderboard() {
        List<LeaderboardDto> leaderboard = leaderboardService.getUserBalanceLeaderboard();
        
        MatchSimulationResponse<List<LeaderboardDto>> response = 
            new MatchSimulationResponse<List<LeaderboardDto>>(null, true, "User leaderboard retrieved successfully.", leaderboard);
            
        return ResponseEntity.ok(response);
    }

    @PostMapping("/simulate-round")
    public String simulateRound() {
        matchSimulationService.simulateCurrentRound();
        return "Round simulated successfully.";
    }

    @PostMapping("/next-round")
    public ResponseEntity<MatchSimulationResponse<String>> nextRound() {
        MatchSimulationResponse<String> serviceResponse = matchSimulationService.advanceToNextRound();
        
        if (serviceResponse.isSuccess()) {
            return ResponseEntity.ok(serviceResponse);
        } else {
            return ResponseEntity.status(400).body(serviceResponse);
        }
    }
}