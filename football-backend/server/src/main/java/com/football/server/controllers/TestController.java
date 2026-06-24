package com.football.server.controllers;

import com.football.server.entities.User;
import com.football.server.service.UserService;
import com.football.server.service.Persist;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private final UserService userService;
    private final Persist persist;

    public TestController(UserService userService, Persist persist) {
        this.userService = userService;
        this.persist = persist;
    }

    @GetMapping("/create-user")
    public String createTestUser() {
        try {
            User user = new User("i3eni", "pass123", "test@test.com");
            persist.save(user); // Uses your working Persist save architecture
            return "Success: Test user created directly in H2 database!";
        } catch (Exception e) {
            return "Database Error: " + e.getMessage();
        }
    }

    @GetMapping("/verify-user")
    public Map<String, Object> verifyUser(@RequestParam String username, @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.getUserByUsernameAndPassword(username, password);
            if (user != null) {
                result.put("status", "success");
                result.put("username", user.getUsername());
                result.put("email", user.getEmail());
                result.put("id", user.getId());
            } else {
                result.put("status", "not found");
            }
        } catch (Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
        }
        return result;
    }
}