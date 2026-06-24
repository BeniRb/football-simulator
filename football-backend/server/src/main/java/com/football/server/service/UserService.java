package com.football.server.service;

import org.springframework.stereotype.Service;
import com.football.server.database.AuthRepository;
import com.football.server.entities.User;

@Service
public class UserService {
    private final AuthRepository authRepository;

    public UserService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public User getUserByUsernameAndPassword(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        
        User user = authRepository.findUserByUsername(username.trim());
        if (user == null) {
            return null;
        }
        
        // Simply check if the incoming hash matches the stored database hash
        if (password.trim().equals(user.getPassword())) {
            return user;
        }
        
        return null;
    }
}