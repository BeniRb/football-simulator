package com.football.server.utils;

public class Constants {
    // HTTP Cookie Names for Authentication
    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    // JWT Configuration
    public static final String JWT_SECRET = "DevOnlySecretIfThisReachesProductionPleasePanicAndReplaceIt12345";
    public static final long JWT_ACCESS_EXPIRATION = 1000L * 60 * 60 * 24;
    public static final long JWT_REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 days
    
    public static final String USER_ID = "userId";
    public static final String TOKEN_TYPE = "tokenType";
    public static final String ACCESS_TOKEN = "ACCESS";
    public static final String REFRESH_TOKEN = "REFRESH";

    public static final int TOKEN_VALID_HOURS = 24;

    private Constants() {
    }
}