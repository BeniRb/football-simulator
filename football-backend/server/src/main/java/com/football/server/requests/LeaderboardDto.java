package com.football.server.requests;


public class LeaderboardDto {
    private int rank;
    private String username;
    private int balance;

    public LeaderboardDto(int rank, String username, int balance) {
        this.rank = rank;
        this.username = username;
        this.balance = balance;
    }

    // Getters and Setters
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }
}