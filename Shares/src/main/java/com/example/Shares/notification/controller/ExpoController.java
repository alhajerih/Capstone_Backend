package com.example.Shares.notification.controller;

import com.example.Shares.notification.entity.ExpoToken;
import com.example.Shares.notification.repository.ExpoTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class ExpoController {
    @Autowired
    private final ExpoTokenRepository expoTokenRepository;
    private final ObjectMapper objectMapper;

    public ExpoController(ExpoTokenRepository expoTokenRepository, ObjectMapper objectMapper) {
        this.expoTokenRepository = expoTokenRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    @RequestMapping("/notification")
    public String registerToken(@RequestBody String requestBody){
        try{
            // Parse JSON
            JsonNode jsonNode = objectMapper.readTree(requestBody);

            // Extract token
            String token = jsonNode.has("token")? jsonNode.get("token").asText():null;
            if(token ==null ||token.isEmpty()){
                return "Invalid request: Token is missing";
            }

            // Check if the token already exists
            Optional<ExpoToken>existingToken = expoTokenRepository.findByToken(token);
            if (existingToken.isPresent()){
                //Update existing token
                ExpoToken expoToken = existingToken.get();
                expoToken.setToken(token);
                expoTokenRepository.save(expoToken);
                return "Token updated successfully";
            }else {
                // Save a new token entry
//                ExpoToken newToken =
            }

        }
    }
}
