package com.football.server.responses;

public class LoginResponse extends BasicResponse {
    private String message;
    private String token;
    private String accessToken;
    private String refreshToken;

    public LoginResponse(Integer errorCode, boolean success, String message) {
        super(success, errorCode);
        this.message = message;
    }

    public LoginResponse(Integer errorCode, boolean success, String message,
                         String accessToken, String refreshToken) {
        super(success, errorCode);
        this.message = message;
        this.token = accessToken; 
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}