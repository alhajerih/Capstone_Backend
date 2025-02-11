package com.example.Shares.hub.controller;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.service.UserService;
import com.example.Shares.hub.bo.HubCardPaymentRequest;
import com.example.Shares.hub.bo.PaymentRequest;
import com.example.Shares.hub.entity.HubEntity;
import com.example.Shares.hub.service.HubService;
import com.example.Shares.wallet.bo.CreateWalletRequest;
import com.example.Shares.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@RestController
@RequestMapping("/api/user")
public class HubController {

        @Autowired
        private WalletService walletService;
        @Autowired
        private UserService userService;
    @Autowired
    private HubService hubService;

    @PostMapping("/pay")
    public ResponseEntity<String> processPayment(
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String token) {

        String jwt = token.substring(7); // Remove "Bearer " prefix

        UserEntity currentUser = userService.getUserFromToken(jwt);
        boolean success = hubService.processPaymentWithChecking(currentUser, request);

        if (success) {
            return ResponseEntity.ok("Transaction successful and recorded.");
        } else {
            return ResponseEntity.badRequest().body("Transaction failed due to insufficient funds or other issues.");
        }
    }
    @PostMapping("/pay-with-hubcard")
    public ResponseEntity<String> payWithHubCard(@RequestBody HubCardPaymentRequest request) {
        boolean success = hubService.processPaymentByHubCard(request);

        if (success) {
            return ResponseEntity.ok("Payment successful and recorded.");
        } else {
            return ResponseEntity.badRequest().body("Payment failed. Please check details and try again.");
        }
    }
    @PostMapping("/reset-card")
    public ResponseEntity<?> resetHubCard(
            @RequestHeader("Authorization") String token
    ) {
        try {
            // 1) Parse the JWT from "Bearer ..."
            String jwt = token.substring(7);

            // 2) Get the current user from the token
            UserEntity currentUser = userService.getUserFromToken(jwt);
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid user token or user not found.");
            }

            // 3) Call the service to reset card details
            HubEntity updatedHub = hubService.resetHubCard(currentUser);

            if (updatedHub == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User does not have a hub to reset.");
            }

            return ResponseEntity.ok("Hub card details have been reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error resetting hub card details: " + e.getMessage());
        }
    }

    @PostMapping("/pay-with-hubcard-ai")
    public boolean smartPay(@RequestBody HubCardPaymentRequest request) {
        // Forward the request to the service
        return hubService.smartPayment(request);
    }



}




