package com.football.server.entities;

public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private int balance;
    private int lockedBalance; // Track funds tied up in active wagers

    public User() {
        this.balance = 1000; // Default starting balance for new users
        this.lockedBalance = 0;
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.balance = 1000; // Default starting balance for new users
        this.lockedBalance = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getBalance() { return balance; }
    public void setBalance(int balance) { this.balance = balance; }

    public int getLockedBalance() { return lockedBalance; }
    public void setLockedBalance(int lockedBalance) { this.lockedBalance = lockedBalance; }
}