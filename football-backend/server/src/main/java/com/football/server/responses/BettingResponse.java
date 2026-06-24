package com.football.server.responses;

import java.util.List;

public class BettingResponse<T> extends BasicResponse {
    private String message;
    private T data;

    public BettingResponse(Integer errorCode, boolean success, String message) {
        super(success, errorCode);
        this.message = message;
    }

    public BettingResponse(Integer errorCode, boolean success, String message, T data) {
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