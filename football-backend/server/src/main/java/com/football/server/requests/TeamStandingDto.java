package com.football.server.requests;

public class TeamStandingDto {
    private String teamName;
    private int matchesPlayed;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int goalDifference;
    private int points;

    // Constructor
    public TeamStandingDto(String teamName) {
        this.teamName = teamName;
    }

    // Helper methods to update stats dynamically
    public void addResult(int teamGoals, int opponentGoals) {
        this.matchesPlayed++;
        this.goalsFor += teamGoals;
        this.goalsAgainst += opponentGoals;
        this.goalDifference = this.goalsFor - this.goalsAgainst;

        if (teamGoals > opponentGoals) {
            this.wins++;
            this.points += 3;
        } else if (teamGoals == opponentGoals) {
            this.draws++;
            this.points += 1;
        } else {
            this.losses++;
        }
    }

    // Getters and Setters
    public String getTeamName() { return teamName; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public int getWins() { return wins; }
    public int getDraws() { return draws; }
    public int getLosses() { return losses; }
    public int getGoalsFor() { return goalsFor; }
    public int getGoalsAgainst() { return goalsAgainst; }
    public int getGoalDifference() { return goalDifference; }
    public int getPoints() { return points; }
}