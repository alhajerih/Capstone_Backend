package com.example.Shares.auth.controller;

import com.example.Shares.auth.bo.LoginResponse;
import com.example.Shares.auth.bo.auth.CreateLoginRequest;
import com.example.Shares.auth.bo.otp.*;
import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class AuthController {


    @Autowired
    private UserService userService;


    @PostMapping("/generate-otp")
    public ResponseEntity<GenerateOtpResponse> generateOtp(@RequestBody GenerateOtpRequest request) {
        GenerateOtpResponse otp = userService.generateOtp(request.getCivilId());
        return ResponseEntity.ok(otp);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<LoginResponse> validateOtp(@RequestBody ValidateOtpRequest request) {
        LoginResponse token = userService.validateOtp(request.getOtp());
        return ResponseEntity.ok(token); // Return the JWT token
    }

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> registerUser(@RequestBody RegisterUserRequest request) {

        userService.registerUser(request.getCivilId(), request.getUsername(), request.getPassword());
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setMessage("User registered successfully.");
        return ResponseEntity.ok(messageResponse);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody CreateLoginRequest loginRequest) {
        // Authenticate the user and generate a token
        String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);

        return loginResponse;
    }


    @GetMapping("/bank-cards")
    public ResponseEntity<List<BankCardEntity>> getBankCards(@RequestHeader("Authorization") String authorizationHeader) {
        // Check Authorization Header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }

        // Extract Token
        String token = authorizationHeader.substring(7); // Remove "Bearer "
        System.out.println("Extracted Token: " + token);

        // Validate and Fetch Bank Cards
        List<BankCardEntity> bankCards = userService.getBankCards(token);
        return ResponseEntity.ok(bankCards);
    }


    @GetMapping("/linked-cards")
    public ResponseEntity<List<BankCardEntity>> getLinkedCards(@RequestHeader("Authorization") String authorizationHeader) {
        // Extract JWT by removing the "Bearer " prefix
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header");
        }
        String token = authorizationHeader.substring(7);

        System.out.println("Extracted Token: " + token);

        // Fetch linked cards using the token
        List<BankCardEntity> cards = userService.getLinkedCards(token);

        return ResponseEntity.ok(cards);
    }

    @PostMapping("/select-cards")
    public ResponseEntity<String> saveSelectedCards(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody List<Long> selectedCardIds) {
        // Extract token and pass it to the service
        String token = authorizationHeader.substring(7);
        userService.saveSelectedCards(token, selectedCardIds);
        return ResponseEntity.ok("Selected cards updated successfully.");
    }


    @GetMapping("/user-details")
    public ResponseEntity<UserEntity> getUserDetails(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        UserEntity user = userService.getUserFromToken(jwt);
        return ResponseEntity.ok(user);
    }

}
