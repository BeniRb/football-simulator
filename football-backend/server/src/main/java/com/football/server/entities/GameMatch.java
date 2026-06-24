package com.football.server.entities;

public class GameMatch {
    private Long id;
    private Long homeTeamId;
    private Long awayTeamId;
    private int roundNumber;
    private int homeScore;
    private int awayScore;
    private boolean isCompleted;
    
    // Environmental modifiers for the simulation math engine
    private String weatherCondition; //"RAINY", "SUNNY", "STORMY"
    private boolean keyInjuryOccurred;

    // Live betting dynamic odds fields
    private double homeOdds;
    private double drawOdds;
    private double awayOdds;

    public GameMatch() {}

    public GameMatch(Long id, Long homeTeamId, Long awayTeamId, int roundNumber, String weatherCondition) {
        this.id = id;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.roundNumber = roundNumber;
        this.homeScore = 0;
        this.awayScore = 0;
        this.isCompleted = false;
        this.weatherCondition = weatherCondition;
        this.keyInjuryOccurred = false;
        // Default placeholders for initialization
        this.homeOdds = 1.0;
        this.drawOdds = 1.0;
        this.awayOdds = 1.0;
    }

    public Long getId() { 
        return id; 
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public Long getHomeTeamId() { 
        return homeTeamId; 
    }
    public void setHomeTeamId(Long homeTeamId) { 
        this.homeTeamId = homeTeamId; 
    }
    public Long getAwayTeamId() { 
        return awayTeamId; 
    }
    public void setAwayTeamId(Long awayTeamId) { 
        this.awayTeamId = awayTeamId; 
    }
    public int getRoundNumber() { 
        return roundNumber; 
    }
    public void setRoundNumber(int roundNumber) { 
        this.roundNumber = roundNumber; 
    }
    public int getHomeScore() { 
        return homeScore; 
    }
    public void setHomeScore(int homeScore) { 
        this.homeScore = homeScore; 
    }
    public int getAwayScore() { 
        return awayScore; 
    }
    public void setAwayScore(int awayScore) { 
        this.awayScore = awayScore; 
    }
    public boolean getIsCompleted() { 
        return isCompleted; 
    }
    public void setIsCompleted(boolean completed) { 
        isCompleted = completed; 
    }
    public String getWeatherCondition() { 
        return weatherCondition; 
    }
    public void setWeatherCondition(String weatherCondition) { 
        this.weatherCondition = weatherCondition; 
    }
    public boolean getIsKeyInjuryOccurred() { 
        return keyInjuryOccurred; 
    }
    public void setIsKeyInjuryOccurred(boolean keyInjuryOccurred) { 
        this.keyInjuryOccurred = keyInjuryOccurred; 
    }

    // Getters and Setters for the new odds fields
    public double getHomeOdds() {
        return homeOdds;
    }
    public void setHomeOdds(double homeOdds) {
        this.homeOdds = homeOdds;
    }
    public double getDrawOdds() {
        return drawOdds;
    }
    public void setDrawOdds(double drawOdds) {
        this.drawOdds = drawOdds;
    }
    public double getAwayOdds() {
        return awayOdds;
    }
    public void setAwayOdds(double awayOdds) {
        this.awayOdds = awayOdds;
    }
}