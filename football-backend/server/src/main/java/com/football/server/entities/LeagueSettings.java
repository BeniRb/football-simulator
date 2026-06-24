package com.football.server.entities;

public class LeagueSettings {
    private Long id;
    private int currentRound;
    private boolean bettingWindowOpen; // Fixed: Changed uppercase 'B' to lowercase 'b'
    private int secondsElapsedInRound;

    public LeagueSettings() {
    }

    public LeagueSettings(long id, int currentRound, boolean bettingWindowOpen, int secondsElapsedInRound) {
        this.id = id;
        this.currentRound = currentRound;
        this.bettingWindowOpen = bettingWindowOpen;
        this.secondsElapsedInRound = secondsElapsedInRound;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getCurrentRound() { return currentRound; }
    public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }

    public boolean isBettingWindowOpen() { return bettingWindowOpen; }
    public void setBettingWindowOpen(boolean bettingWindowOpen) { this.bettingWindowOpen = bettingWindowOpen; }

    public int getSecondsElapsedInRound() { return secondsElapsedInRound; }
    public void setSecondsElapsedInRound(int secondsElapsedInRound) { this.secondsElapsedInRound = secondsElapsedInRound; }
}