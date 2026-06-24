package com.football.server.responses;

import com.football.server.entities.Bet;
import java.util.List;

public class ProfileResponse {
    private boolean success;
    private String username;
    private String email;
    private int balance;
    private int lockedBalance;
    private int wins;
    private int losses;
    private int netProfitLoss;
    private int activeBetsCount;
    private List<Bet> bets; // Added field

    // Updated Constructor to accept 10 parameters
    public ProfileResponse(boolean success, String username, String email, int balance, 
                           int lockedBalance, int wins, int losses, int netProfitLoss, 
                           int activeBetsCount, List<Bet> bets) {
        this.success = success;
        this.username = username;
        this.email = email;
        this.balance = balance;
        this.lockedBalance = lockedBalance;
        this.wins = wins;
        this.losses = losses;
        this.netProfitLoss = netProfitLoss;
        this.activeBetsCount = activeBetsCount;
        this.bets = bets;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    public int getLockedBalance() { return lockedBalance; }
    public void setLockedBalance(int lockedBalance) { this.lockedBalance = lockedBalance; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getNetProfitLoss() { return netProfitLoss; }
    public void setNetProfitLoss(int netProfitLoss) { this.netProfitLoss = netProfitLoss; }

    public int getActiveBetsCount() { return activeBetsCount; }
    public void setActiveBetsCount(int activeBetsCount) { this.activeBetsCount = activeBetsCount; }

    public List<Bet> getBets() { return bets; }
    public void setBets(List<Bet> bets) { this.bets = bets; }
}