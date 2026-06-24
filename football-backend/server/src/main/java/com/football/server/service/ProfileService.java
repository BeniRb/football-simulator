package com.football.server.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.football.server.database.AuthRepository;
import com.football.server.entities.User;
import com.football.server.entities.Bet;
import com.football.server.responses.ProfileResponse;

import java.util.List;

@Service
public class ProfileService {

    private final AuthRepository authRepository;
    private final BettingService bettingService;

    public ProfileService(AuthRepository authRepository, BettingService bettingService) {
        this.authRepository = authRepository;
        this.bettingService = bettingService;
    }

    @Transactional(readOnly = true)
    public ProfileResponse getProfileData(Long userId, String token) {
        User user = authRepository.findUserById(userId);
        if (user == null) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        List<Bet> userBets = bettingService.getUserBettingHistory(user.getId());
        return buildProfileResponse(user, userBets);
    }

    @Transactional
    public ProfileResponse deposit(Long userId, String token, int amount) {
        User user = authRepository.findUserById(userId);
        if (user == null) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        user.setBalance(user.getBalance() + amount);
        authRepository.saveUser(user);

        List<Bet> userBets = bettingService.getUserBettingHistory(user.getId());
        return buildProfileResponse(user, userBets);
    }

    @Transactional
    public ProfileResponse withdraw(Long userId, String token, int amount) {
        User user = authRepository.findUserById(userId);
        if (user == null) {
            return new ProfileResponse(false, null, null, 0, 0, 0, 0, 0, 0, null);
        }

        if (user.getBalance() < amount) {
            // Fixed parameter order: success, username, email, balance, lockedBalance, wins, losses, netProfitLoss, activeBetsCount, bets
            return new ProfileResponse(false, "INSUFFICIENT_FUNDS", null, user.getBalance(), user.getLockedBalance(), 0, 0, 0, 0, null);
        }

        user.setBalance(user.getBalance() - amount);
        authRepository.saveUser(user);

        List<Bet> userBets = bettingService.getUserBettingHistory(user.getId());
        return buildProfileResponse(user, userBets);
    }

    private ProfileResponse buildProfileResponse(User user, List<Bet> userBets) {
        int wins = 0; 
        int losses = 0; 
        int netProfitLoss = 0;
        int activeBetsCount = 0;

        if (userBets != null) {
            for (Bet bet : userBets) {
                if (bet.getIsSettled()) {
                    if (bet.getIsWin()) {
                        wins++;
                        int payout = (int) Math.round(bet.getWagerAmount() * bet.getOddsAtPlacement());
                        netProfitLoss += (payout - bet.getWagerAmount());
                    } else {
                        losses++;
                        netProfitLoss -= bet.getWagerAmount();
                    }
                } else {
                    activeBetsCount++;
                }
            }
        }
        return new ProfileResponse(true, user.getUsername(), user.getEmail(), user.getBalance(), user.getLockedBalance(), wins, losses, netProfitLoss, activeBetsCount, userBets);
    }
}