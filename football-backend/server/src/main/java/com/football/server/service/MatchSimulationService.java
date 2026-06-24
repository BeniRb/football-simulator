package com.football.server.service;

import com.football.server.entities.GameMatch;
import com.football.server.entities.LeagueSettings;
import com.football.server.entities.Team;
import com.football.server.responses.MatchSimulationResponse;
import com.football.server.responses.OddsResponse;
import com.football.server.utils.Errors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Random;

@Service
public class MatchSimulationService {

    private final Persist persist;
    private final BettingService bettingService;
    private final OddsService oddsService;
    private final Random random = new Random();

    public MatchSimulationService(Persist persist, BettingService bettingService, OddsService oddsService) {
        this.persist = persist;
        this.bettingService = bettingService;
        this.oddsService = oddsService;
    }

    /**
     * BACKGROUND CRON TIMER LOOP
     * Automatically wakes up every second to process the season matches.
     */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void autoProcessRoundLoop() {
        LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.LeagueSettings")
                .uniqueResult();

        if (settings == null) return;

        int currentRound = settings.getCurrentRound();

        // Increment our clock tracking parameter by 1 second
        int elapsed = settings.getSecondsElapsedInRound() + 1;
        settings.setSecondsElapsedInRound(elapsed);
        persist.save(settings);

        // 1. Right at the start of the round, announce the full remaining window time
        if (elapsed == 1) {
            System.out.println("Betting window open, 20 seconds remaining.");
        }

        // PHASE 1: At 20 seconds, close the window and print your waiting notice once
        if (elapsed == 20) {
            settings.setBettingWindowOpen(false);
            persist.save(settings);
            System.out.println("Betting window closed, wait for next match.");
        }

        // PHASE 2: At 60 seconds, run simulations, settle bets, and advance the round
        if (elapsed >= 60) {
            System.out.println("60 seconds reached. Simulating matches for round " + currentRound + "...");
            
            // 1. Run simulation math and settle payouts
            MatchSimulationResponse<Void> simResponse = simulateCurrentRound();
            
            if (!simResponse.isSuccess()) {
                System.out.println("Scheduler Error [" + simResponse.getErrorCode() + "]: " + simResponse.getMessage());
                settings.setSecondsElapsedInRound(0);
                persist.save(settings);
                return;
            }
            
            // 2. Advance to the next round structure and generate new odds (or resets to a new season)
            MatchSimulationResponse<String> advancementResponse = advanceToNextRound();
            if (advancementResponse.isSuccess()) {
                System.out.println("Scheduler Success: " + advancementResponse.getData());
            } else {
                System.out.println("Scheduler Error [" + advancementResponse.getErrorCode() + "]: " + advancementResponse.getMessage());
            }
            
            // 3. Reset our timer back to 0 for the upcoming round window
            settings.setSecondsElapsedInRound(0);
            persist.save(settings);
        }
    }

    /**
     * MATCH SIMULATOR & PAYOUT LOGIC
     * Fetches current unplayed fixtures, calculates scores, and triggers bet processing.
     */
    @Transactional
    public MatchSimulationResponse<Void> simulateCurrentRound() {
        LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.LeagueSettings")
                .uniqueResult();

        if (settings == null) {
            return new MatchSimulationResponse<>(Errors.ERROR_LEAGUE_SETTINGS_NOT_FOUND, false, "Critical: League settings are missing.");
        }
        int currentRound = settings.getCurrentRound();

        List<GameMatch> matches = persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.GameMatch WHERE roundNumber = :round AND isCompleted = false", GameMatch.class)
                .setParameter("round", currentRound)
                .list();

        if (matches.isEmpty()) {
            return new MatchSimulationResponse<>(Errors.ERROR_NO_MATCHES_FOR_ROUND, false, "Data mismatch: No uncompleted matches found for active round " + currentRound);
        }

        for (GameMatch match : matches) {
            Team home = persist.loadObject(Team.class, match.getHomeTeamId());
            Team away = persist.loadObject(Team.class, match.getAwayTeamId());

            String weather = generateRandomWeather();
            boolean injury = random.nextInt(100) < 15;

            int homeGoals = calculateGoals(home.getSkillLevel(), away.getSkillLevel(), weather, injury, true);
            int awayGoals = calculateGoals(away.getSkillLevel(), home.getSkillLevel(), weather, injury, false);

            match.setHomeScore(homeGoals);
            match.setAwayScore(awayGoals);
            match.setWeatherCondition(weather);
            match.setIsKeyInjuryOccurred(injury);
            match.setIsCompleted(true);
            persist.save(match);

            System.out.println(String.format("⚽ Match Result: %s %d - %d %s [%s%s]", 
                    home.getName(), homeGoals, awayGoals, away.getName(), weather, injury ? " | INJURY ALERT" : ""));

            updateTeamStats(home, away, homeGoals, awayGoals);

            bettingService.settleBetsForMatch(match);
        }

        return new MatchSimulationResponse<>(null, true, "Round " + currentRound + " matches simulated and settled successfully.");
    }

    /**
     * POISSON GOAL COEFFICIENT MATH
     */
    private int calculateGoals(int attackerSkill, int defenderSkill, String weather, boolean injury, boolean isHome) {
        double skillFactor = (attackerSkill - defenderSkill) * 0.2;
        double homeAdvantage = isHome ? 0.3 : 0.0;
        
        double weatherModifier = weather.equals("RAINY") ? -0.2 : (weather.equals("STORMY") ? -0.4 : 0.0);
        double injuryModifier = injury ? -0.4 : 0.0;

        double expectedGoals = 1.5 + skillFactor + homeAdvantage + weatherModifier + injuryModifier;
        if (expectedGoals < 0) expectedGoals = 0.5;

        int goals = 0;
        double p = Math.exp(-expectedGoals);
        double g = random.nextDouble();
        while (g > p) {
            goals++;
            g -= p;
            p *= expectedGoals / goals;
            if (goals > 7) break;
        }
        return goals;
    }

    private String generateRandomWeather() {
        int roll = random.nextInt(100);
        if (roll < 70) return "SUNNY";
        if (roll < 90) return "RAINY";
        return "STORMY";
    }

    private void updateTeamStats(Team home, Team away, int homeGoals, int awayGoals) {
        home.setGoalsScored(home.getGoalsScored() + homeGoals);
        home.setGoalsConceded(home.getGoalsConceded() + awayGoals);
        away.setGoalsScored(away.getGoalsScored() + awayGoals);
        away.setGoalsConceded(away.getGoalsConceded() + homeGoals);

        if (homeGoals > awayGoals) {
            home.setPoints(home.getPoints() + 3);
        } else if (awayGoals > homeGoals) {
            away.setPoints(away.getPoints() + 3);
        } else {
            home.setPoints(home.getPoints() + 1);
            away.setPoints(away.getPoints() + 1);
        }

        persist.save(home);
        persist.save(away);
    }

    /**
     * ROUND SEQUENCE MANAGEMENT ROUTINE
     */
    @Transactional
    public MatchSimulationResponse<String> advanceToNextRound() {
        LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.LeagueSettings")
                .uniqueResult();

        if (settings == null) {
            return new MatchSimulationResponse<>(Errors.ERROR_LEAGUE_SETTINGS_NOT_FOUND, false, "League settings not found.");
        }

        int currentRound = settings.getCurrentRound();

        Long uncompletedCount = persist.getQuerySession()
                .createQuery("SELECT count(m) FROM com.football.server.entities.GameMatch m WHERE m.roundNumber = :round AND m.isCompleted = false", Long.class)
                .setParameter("round", currentRound)
                .uniqueResult();

        if (uncompletedCount > 0) {
            return new MatchSimulationResponse<>(Errors.ERROR_NO_MATCHES_FOR_ROUND, false, "Cannot advance round. Matches are outstanding.");
        }

        // AUTOMATED ENDLESS SEASONS LOOP
        if (currentRound >= 7) {
            System.out.println("🏆 SEASON OVER! Resetting league infrastructure for a new season... 🏆");
            
            settings.setCurrentRound(1);
            settings.setBettingWindowOpen(true);
            settings.setSecondsElapsedInRound(0);
            persist.save(settings);

            persist.getQuerySession()
                    .createQuery("UPDATE com.football.server.entities.Team SET points = 0, goalsScored = 0, goalsConceded = 0")
                    .executeUpdate();

            persist.getQuerySession()
                    .createQuery("UPDATE com.football.server.entities.GameMatch SET isCompleted = false, homeScore = 0, awayScore = 0")
                    .executeUpdate();

            List<GameMatch> roundOneMatches = persist.getQuerySession()
                    .createQuery("FROM com.football.server.entities.GameMatch WHERE roundNumber = 1", GameMatch.class)
                    .list();
            for (GameMatch match : roundOneMatches) {
                OddsResponse<Void> oddsResponse = oddsService.calculateAndSaveOdds(match);
                if (!oddsResponse.isSuccess()) {
                    System.out.println("Odds Warning [" + oddsResponse.getErrorCode() + "]: " + oddsResponse.getMessage());
                }
            }

            return new MatchSimulationResponse<>(null, true, "Success", "A new season has officially started! Standing tables reset, fixtures cleared, and Round 1 is live.");
        }

        int nextRoundNumber = currentRound + 1;

        settings.setCurrentRound(nextRoundNumber);
        settings.setBettingWindowOpen(true);
        persist.save(settings);

        List<GameMatch> nextRoundMatches = persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.GameMatch WHERE roundNumber = :nextRound", GameMatch.class)
                .setParameter("nextRound", nextRoundNumber)
                .list();

        for (GameMatch nextMatch : nextRoundMatches) {
            OddsResponse<Void> oddsResponse = oddsService.calculateAndSaveOdds(nextMatch);
            if (!oddsResponse.isSuccess()) {
                System.out.println("Odds Warning [" + oddsResponse.getErrorCode() + "]: " + oddsResponse.getMessage());
            }
        }

        return new MatchSimulationResponse<>(null, true, "Success", "Successfully advanced to round " + nextRoundNumber + ". Betting window is now open with live odds calculated.");
    }

    @org.springframework.context.event.EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    @Transactional
    public void resetClockOnStartup() {
        LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.LeagueSettings")
                .uniqueResult();

        if (settings != null) {
            settings.setSecondsElapsedInRound(0);
            settings.setBettingWindowOpen(true);
            persist.save(settings);

            // TRIGGER ODDS CALCULATION IMMEDIATELY ON STARTUP
            List<GameMatch> currentMatches = persist.getQuerySession()
                    .createQuery("FROM com.football.server.entities.GameMatch WHERE roundNumber = :round", GameMatch.class)
                    .setParameter("round", settings.getCurrentRound())
                    .list();

            for (GameMatch match : currentMatches) {
                oddsService.calculateAndSaveOdds(match);
            }
            
            System.out.println("System Startup: Odds generated and betting window opened for Round " + settings.getCurrentRound());
        }
    }
}