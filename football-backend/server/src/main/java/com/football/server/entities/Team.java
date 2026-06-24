package com.football.server.entities;

public class Team {
    private Long id;
    private String name;
    private int skillLevel;
    private int points; 
    private int goalsScored;
    private int goalsConceded;

    public Team() {}

    public Team(Long id, String name, int skillLevel, int points, int goalsScored, int goalsConceded) {
        this.id = id;
        this.name = name;
        this.skillLevel = skillLevel;
        this.points = points;
        this.goalsScored = goalsScored;
        this.goalsConceded = goalsConceded;
    }

    
    public Long getId() { 
     return id;
    }
    public void setId(Long id) { 
        this.id = id; 
    }
    public String getName() { 
        return name; 
    }
    public void setName(String name) { 
        this.name = name; 
    }

    public int getSkillLevel() { 
        return skillLevel; 
    }
    public void setSkillLevel(int skillLevel) { 
        this.skillLevel = skillLevel; 
    }

    public int getPoints() { 
        return points; 
    }
    public void setPoints(int points) { 
        this.points = points; 
    }

    public int getGoalsScored() { 
        return goalsScored; 
    }
    public void setGoalsScored(int goalsScored) { 
        this.goalsScored = goalsScored; 
    }

    public int getGoalsConceded() { 
        return goalsConceded; 
    }
    public void setGoalsConceded(int goalsConceded) { 
        this.goalsConceded = goalsConceded; 
    }

    // Helper method for the leaderboard tie-breaker sorting rules
    public int getGoalDifference() {
        return this.goalsScored - this.goalsConceded;
    }
}