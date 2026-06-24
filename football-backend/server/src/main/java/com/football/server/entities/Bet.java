package com.football.server.entities;

public class Bet {
    private Long id;
    private String token; // The user token who placed the bet
    private Long userId; // The user ID who placed the bet
    private Long matchId;
    private String prediction; // "HOME_WIN", "AWAY_WIN", "DRAW"
    private int wagerAmount;
    private double oddsAtPlacement; // Changed from int to double
    private boolean isSettled;
    private boolean isWin;

    public Bet() {}

    public Bet(Long id, String token, Long userId, Long matchId, String prediction, int wagerAmount, double oddsAtPlacement) {
        this.id = id;
        this.token = token;
        this.userId = userId;
        this.matchId = matchId;
        this.prediction = prediction;
        this.wagerAmount = wagerAmount;
        this.oddsAtPlacement = oddsAtPlacement;
        this.isSettled = false;
        this.isWin = false;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getPrediction() { return prediction; }
    public void setPrediction(String prediction) { this.prediction = prediction; }

    public int getWagerAmount() { return wagerAmount; }
    public void setWagerAmount(int wagerAmount) { this.wagerAmount = wagerAmount; }

    public double getOddsAtPlacement() { return oddsAtPlacement; }
    public void setOddsAtPlacement(double oddsAtPlacement) { this.oddsAtPlacement = oddsAtPlacement; }

    // Alias helper getter used by BettingService settlement loops
    public double getOdds() { return oddsAtPlacement; }

    public boolean getIsSettled() { return isSettled; }
    public void setIsSettled(boolean settled) { isSettled = settled; }

    public boolean getIsWin() { return isWin; }
    public void setIsWin(boolean win) { isWin = win; }
    
    // Calculates payout return value dynamically
    public int getPotentialPayout() {
        return (int) Math.round(this.wagerAmount * this.oddsAtPlacement);
    }
}