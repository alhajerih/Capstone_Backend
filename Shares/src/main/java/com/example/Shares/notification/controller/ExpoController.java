package com.example.Shares.notification.controller;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.repository.UserRepository;
import com.example.Shares.notification.entity.ExpoToken;
import com.example.Shares.notification.repository.ExpoTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/setup")
public class ExpoController {

    @Autowired
    private final ExpoTokenRepository expoTokenRepository;

    @Autowired
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    public ExpoController(ExpoTokenRepository expoTokenRepository, UserRepository userRepository, ObjectMapper objectMapper) {
        this.expoTokenRepository = expoTokenRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/notification")
    public String registerToken(@RequestBody String requestBody) {
        System.out.println("requestBody: " + requestBody);
        try {
            // Parse JSON
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            // Extract token
            String token = jsonNode.has("token") ? jsonNode.get("token").asText() : null;
            if (token == null || token.isEmpty()) {
                return "Invalid request: Token is missing";
            }

            // Extract userID
            Long userId = jsonNode.has("userId") ? jsonNode.get("userId").asLong() : null;
            if (userId == null) {
                return "Invalid request: User ID is missing";
            }

            // Fetch the user from the database
            Optional<UserEntity> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                return "Invalid request: User not found";
            }

            UserEntity user = userOptional.get(); // Get the user entity

            // Check if this token is already registered for any user
            Optional<ExpoToken> existingTokenOptional = expoTokenRepository.findByToken(token);
            if (existingTokenOptional.isPresent()) {
                ExpoToken existingToken = existingTokenOptional.get();

                // If the token already belongs to this user, no need to change anything
                if (existingToken.getUser().getId().equals(userId)) {
                    return "Token already registered for this user, no changes made.";
                } else {
                    // If token is already used by another user, update ownership
                    existingToken.setUser(user);
                    expoTokenRepository.save(existingToken);
                    return "Token reassigned to the correct user";
                }
            }

            // Check if this user already has a registered token
            Optional<ExpoToken> userTokenOptional = expoTokenRepository.findByUserId(userId);
            if (userTokenOptional.isPresent()) {
                ExpoToken userToken = userTokenOptional.get();

                // If the token is different, update it
                if (!userToken.getToken().equals(token)) {
                    userToken.setToken(token);
                    expoTokenRepository.save(userToken);
                    return "User's token updated successfully";
                } else {
                    return "Token already registered for this user, no changes made.";
                }
            }

            // If neither token nor user exists, create a new record
            ExpoToken newToken = new ExpoToken();
            newToken.setToken(token);
            newToken.setUser(user);
            expoTokenRepository.save(newToken);
            return "Token registered successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing request: " + e.getMessage();
        }
    }
}
