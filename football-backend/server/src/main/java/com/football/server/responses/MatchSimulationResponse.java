package com.football.server.responses;

public class MatchSimulationResponse<T> extends BasicResponse {
    private String message;
    private T data;

    public MatchSimulationResponse(Integer errorCode, boolean success, String message) {
        super(success, errorCode);
        this.message = message;
    }

    public MatchSimulationResponse(Integer errorCode, boolean success, String message, T data) {
        super(success, errorCode);
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}