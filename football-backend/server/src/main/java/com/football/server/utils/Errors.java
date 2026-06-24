package com.football.server.utils;

public class Errors {
    // Authentication & Authorization Errors
    public static final int ERROR_INVALID_TOKEN = 2000;
    public static final int ERROR_UNAUTHORIZED_ACTION = 2001;

    // Validation & Credentials Errors
    public static final int ERROR_EMPTY_FIELD = 3000;
    public static final int ERROR_WRONG_CREDENTIALS = 3001;
    public static final int ERROR_UPDATE_TOKEN_FAILED = 3002;
    public static final int ERROR_INVALID_ID = 3003;

    // Betting Errors
    public static final int ERROR_MATCH_NOT_FOUND = 4001;
    public static final int ERROR_BETTING_CLOSED = 4002;
    public static final int ERROR_INSUFFICIENT_FUNDS = 4003;
    
    // Odds Calculation Errors
    public static final int ERROR_TEAM_NOT_FOUND = 4101;
    public static final int ERROR_NULL_MATCH_REFERENCE = 4102;

    // Simulation Errors
    public static final int ERROR_LEAGUE_SETTINGS_NOT_FOUND = 5001;
    public static final int ERROR_NO_MATCHES_FOR_ROUND = 5002;

    // User Management Errors
    public static final int ERROR_USER_ALREADY_EXISTS = 6001;
    public static final int ERROR_USER_NOT_FOUND = 6002;

    //settings errors
    public static final int ERROR_SETTINGS_NOT_FOUND = 7001;

    // System Errors
    public static final int ERROR_TEST_ADD_DATA_FAILED = 9001;

    private Errors() {
    }
}