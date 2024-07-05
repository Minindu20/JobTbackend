package com.example.jobTpro.controller;


import ch.qos.logback.classic.Logger;
import com.example.jobTpro.entity.User;
import com.example.jobTpro.repo.UserRepository;
import com.example.jobTpro.utils.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user){
        User savedUser = userRepository.save(user);
        String token = jwtTokenUtil.generateToken(savedUser);
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("email", savedUser.getEmail());
        userResponse.put("lastName", savedUser.getLastName());
        userResponse.put("location", savedUser.getLocation());
        userResponse.put("name", savedUser.getName());
        userResponse.put("token", token);

        response.put("user", userResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody User user){
        String email = user.getEmail();
        String password = user.getPassword();
        User savedUser = userRepository.findByEmail(email);
        if (savedUser == null ||   !password.equals(savedUser.getPassword())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("msg", "Invalid Credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
            String token = jwtTokenUtil.generateToken(savedUser);
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> userResponse = new HashMap<>();
            userResponse.put("email", savedUser.getEmail());
            userResponse.put("lastName", savedUser.getLastName());
            userResponse.put("location", savedUser.getLocation());
            userResponse.put("name", savedUser.getName());
            userResponse.put("token", token);

            response.put("user", userResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestHeader("Authorization") String token, @RequestBody User user) {
        if (token == null || !token.startsWith("Bearer ")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("msg", "Invalid Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        String authToken = token.substring(7);
        int userId;
        try {
            userId = jwtTokenUtil.getUserIdFromToken(authToken);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("msg", "Invalid Token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        User savedUser = userRepository.findById(userId);

        if (savedUser == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("msg", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

     
        if (user.getName() != null) {
            savedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            savedUser.setEmail(user.getEmail());
        }
        if (user.getLocation() != null) {
            savedUser.setLocation(user.getLocation());
        }
        if (user.getLastName() != null) {
            savedUser.setLastName(user.getLastName());
        }

        User updatedUser = userRepository.save(savedUser);
        String newToken = jwtTokenUtil.generateToken(updatedUser);

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("email", updatedUser.getEmail());
        userResponse.put("lastName", updatedUser.getLastName());
        userResponse.put("location", updatedUser.getLocation());
        userResponse.put("name", updatedUser.getName());
        userResponse.put("token", newToken);

        response.put("user", userResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
