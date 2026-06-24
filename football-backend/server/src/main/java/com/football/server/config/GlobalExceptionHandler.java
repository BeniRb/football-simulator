package com.football.server.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.football.server.responses.MatchSimulationResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Catches generic server crashes (NullPointer, Database exceptions, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MatchSimulationResponse<String>> handleAllExceptions(Exception ex) {
        MatchSimulationResponse<String> response = new MatchSimulationResponse<>(
            5000, // Generic internal error code
            false, 
            "Internal server error: " + ex.getMessage(), 
            null
        );
        return ResponseEntity.status(500).body(response);
    }
}