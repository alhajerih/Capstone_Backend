package com.example.Shares.controller;

import com.example.Shares.bo.CreateUserRequest;
import com.example.Shares.bo.UserResponse;
import com.example.Shares.bo.auth.AuthenticationResponse;
import com.example.Shares.bo.auth.CreateLoginRequest;
import com.example.Shares.bo.auth.LogoutResponse;
import com.example.Shares.bo.otp.BankCardRequest;
import com.example.Shares.bo.otp.GenerateOtpRequest;
import com.example.Shares.bo.otp.RegisterUserRequest;
import com.example.Shares.bo.otp.ValidateOtpRequest;
import com.example.Shares.entity.BankCardEntity;
import com.example.Shares.entity.UserEntity;
import com.example.Shares.service.UserService;
//import com.example.Shares.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class AuthController {


    @Autowired
    private UserService userService;



    @PostMapping("/generate-otp")
    public ResponseEntity<String> generateOtp(@RequestBody GenerateOtpRequest request) {
        String otp = userService.generateOtp(request.getCivilId());
        return ResponseEntity.ok("OTP sent to registered phone number: "+otp);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOtp(@RequestBody ValidateOtpRequest request) {
        String token = userService.validateOtp(request.getOtp());
        return ResponseEntity.ok(token); // Return the JWT token
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterUserRequest request) {
        userService.registerUser(request.getCivilId(), request.getUsername(), request.getPassword());
        return ResponseEntity.ok("User registered successfully.");
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
