package com.football.server.requests;

public class BetDto {
    private Long matchId;
    private String prediction;
    private int wagerAmount;
    private double clientOdds;

    // Getters and Setters
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public String getPrediction() { return prediction; }
    public void setPrediction(String prediction) { this.prediction = prediction; }

    public int getWagerAmount() { return wagerAmount; }
    public void setWagerAmount(int wagerAmount) { this.wagerAmount = wagerAmount; }

    public double getClientOdds() { return clientOdds; }
    public void setClientOdds(double clientOdds) { this.clientOdds = clientOdds; }
}