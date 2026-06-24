package com.football.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.football.server.entities.User;
import com.football.server.entities.Bet;
import com.football.server.entities.GameMatch;
import com.football.server.entities.LeagueSettings;
import com.football.server.responses.BettingResponse;
import com.football.server.security.JwtService;
import com.football.server.utils.Errors;
import java.util.List;

@Service
public class BettingService {

    private final Persist persist;
    private final JwtService jwtService;

    public BettingService(Persist persist, JwtService jwtService) {
        this.persist = persist;
        this.jwtService = jwtService;
    }

    @Transactional
    public BettingResponse<Void> placeBet(String token, Long matchId, String prediction, int wagerAmount, double clientOdds) {
        // 1. Check if the active round betting window is open
        LeagueSettings settings = (LeagueSettings) persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.LeagueSettings")
                .uniqueResult();

        if (settings == null || !settings.isBettingWindowOpen()) {
            return new BettingResponse<>(Errors.ERROR_BETTING_CLOSED, false, "Betting is currently closed for this round.");
        }

        // 2. Validate token and resolve user directly via JWT claims
        if (!jwtService.isTokenValid(token)) {
            return new BettingResponse<>(Errors.ERROR_INVALID_TOKEN, false, "Invalid session token.");
        }

        Integer userId = jwtService.extractUserId(token);
        if (userId == null) {
            return new BettingResponse<>(Errors.ERROR_INVALID_TOKEN, false, "Invalid session token.");
        }

        User user = persist.loadObject(User.class, userId);
        if (user == null) {
            return new BettingResponse<>(Errors.ERROR_USER_NOT_FOUND, false, "User not found.");
        }

        // 3. Verify balance
        if (user.getBalance() < wagerAmount) {
            return new BettingResponse<>(Errors.ERROR_INSUFFICIENT_FUNDS, false, "Insufficient funds. Current balance: " + user.getBalance());
        }

        // 4. Verify match state and retrieve server-side reference odds
        GameMatch match = persist.loadObject(GameMatch.class, matchId);
        if (match == null) {
            return new BettingResponse<>(Errors.ERROR_MATCH_NOT_FOUND, false, "Match not found.");
        }
        if (match.getIsCompleted()) {
            return new BettingResponse<>(Errors.ERROR_BETTING_CLOSED, false, "Cannot bet on a completed match.");
        }

        // 5. SECURITY GUARD: Server-side validation of odds against manipulation attempts
        double officialServerOdds;
        switch (prediction.toUpperCase()) {
            case "HOME_WIN":
                officialServerOdds = match.getHomeOdds();
                break;
            case "DRAW":
                officialServerOdds = match.getDrawOdds();
                break;
            case "AWAY_WIN":
                officialServerOdds = match.getAwayOdds();
                break;
            default:
                return new BettingResponse<>(Errors.ERROR_EMPTY_FIELD, false, "Invalid prediction target type. Must be HOME_WIN, DRAW, or AWAY_WIN.");
        }

        // Allow a small delta allowance for floating-point calculation variances
        if (Math.abs(officialServerOdds - clientOdds) > 0.01) {
            return new BettingResponse<>(Errors.ERROR_UPDATE_TOKEN_FAILED, false, "Odds discrepancy detected. The live odds have updated. Please try again.");
        }

        // 6. Commit wallet balances (Deduct from balance, add to locked balance)
        user.setBalance(user.getBalance() - wagerAmount);
        user.setLockedBalance(user.getLockedBalance() + wagerAmount);
        persist.save(user);

        // Convert Integer userId to Long to safely assign to the updated entity schema
        Long standardUserId = userId.longValue();

        // Saves the verified server odds rather than trusted frontend input
        Bet bet = new Bet(null, token, standardUserId, matchId, prediction, wagerAmount, officialServerOdds);
        persist.save(bet);

        return new BettingResponse<>(null, true, "Bet placed successfully. Remaining balance: " + user.getBalance());
    }

    @Transactional
    public void settleBetsForMatch(GameMatch match) {
        List<Bet> activeBets = persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.Bet WHERE matchId = :matchId AND isSettled = false", Bet.class)
                .setParameter("matchId", match.getId())
                .list();

        String actualOutcome;
        if (match.getHomeScore() > match.getAwayScore()) {
            actualOutcome = "HOME_WIN";
        } else if (match.getAwayScore() > match.getHomeScore()) {
            actualOutcome = "AWAY_WIN";
        } else {
            actualOutcome = "DRAW";
        }

        for (Bet bet : activeBets) {
            boolean won = bet.getPrediction().equalsIgnoreCase(actualOutcome);
            bet.setIsWin(won);
            bet.setIsSettled(true);

            // Always resolve the locked balance tracking back out of the token profile
            if (jwtService.isTokenValid(bet.getToken())) {
                Integer userId = jwtService.extractUserId(bet.getToken());
                if (userId != null) {
                    User user = persist.loadObject(User.class, userId);
                    if (user != null) {
                        // Free the original wager amount from locked pool
                        user.setLockedBalance(Math.max(0, user.getLockedBalance() - bet.getWagerAmount()));
                        
                        if (won) {
                            int payout = (int) Math.round(bet.getWagerAmount() * bet.getOddsAtPlacement());
                            user.setBalance(user.getBalance() + payout);
                        }
                        persist.save(user);
                    }
                }
            }
            persist.save(bet);
        }
    }

    @Transactional(readOnly = true)
    public List<Bet> getUserBettingHistory(Long userId) {
        return persist.getQuerySession()
                .createQuery("FROM com.football.server.entities.Bet WHERE userId = :userId ORDER BY id DESC", Bet.class)
                .setParameter("userId", userId)
                .list();
    }
}