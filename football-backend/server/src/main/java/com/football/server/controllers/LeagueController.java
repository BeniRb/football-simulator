package com.football.server.controllers;

import com.football.server.entities.GameMatch;
import com.football.server.entities.LeagueSettings;
import com.football.server.entities.Team;
import com.football.server.responses.LeagueResponse;
import com.football.server.service.LeagueService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.football.server.service.Persist;
import com.football.server.utils.Errors;

import java.util.List;

@RestController
@RequestMapping("/league")
public class LeagueController {

    private final LeagueService leagueService;
    private final Persist persist; // Assuming you have a Persist service for database access

    public LeagueController(LeagueService leagueService, Persist persist) {
        this.leagueService = leagueService;
        this.persist = persist;
    }

    @GetMapping("/fixtures")
    public LeagueResponse<List<GameMatch>> getCurrentRoundFixtures() {
        return leagueService.getCurrentRoundFixtures();
    }

    @GetMapping("/standings")
    public LeagueResponse<List<Team>> getStandings() {
        return leagueService.getStandings();
    }
    @GetMapping("/history")
    public LeagueResponse<List<GameMatch>> getAllCompletedFixtures() {
        return leagueService.getAllCompletedFixtures();
    }
    @GetMapping("/current-round")
    public int getCurrentRound() {
        return leagueService.getCurrentRound(); // Add this method to your service
    }
    @GetMapping("/settings")
    public LeagueResponse<?> getLeagueSettings() {
        try {
            LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                    .createQuery("FROM com.football.server.entities.LeagueSettings")
                    .uniqueResult();
            
            if (settings == null) {
                return new LeagueResponse<>(Errors.ERROR_SETTINGS_NOT_FOUND, false, "Settings not found.");
            }

            // Return a Map to avoid any entity serialization/mapping issues
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("currentRound", settings.getCurrentRound());
            data.put("bettingWindowOpen", settings.isBettingWindowOpen());
            data.put("secondsElapsedInRound", settings.getSecondsElapsedInRound());
            
            return new LeagueResponse<>(null, true, "Success", data);
        } catch (Exception e) {
            return new LeagueResponse<>(500, false, "Error: " + e.getMessage());
        }
    }
}