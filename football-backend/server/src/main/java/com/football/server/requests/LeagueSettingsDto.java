package com.football.server.requests;

public class LeagueSettingsDto {
    private int currentRound;
    private int secondsElapsedInRound;
    private boolean bettingWindowOpen;

    public LeagueSettingsDto(int currentRound, int secondsElapsedInRound, boolean bettingWindowOpen) {
        this.currentRound = currentRound;
        this.secondsElapsedInRound = secondsElapsedInRound;
        this.bettingWindowOpen = bettingWindowOpen;
    }
    // Getters
    public int getCurrentRound() { return currentRound; }
    public int getSecondsElapsedInRound() { return secondsElapsedInRound; }
    public boolean isBettingWindowOpen() { return bettingWindowOpen; }
    
}