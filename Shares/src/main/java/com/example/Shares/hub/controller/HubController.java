package com.example.Shares.hub.controller;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.service.UserService;
import com.example.Shares.hub.bo.HubCardPaymentRequest;
import com.example.Shares.hub.bo.PaymentRequest;
import com.example.Shares.hub.service.HubService;
import com.example.Shares.wallet.bo.CreateWalletRequest;
import com.example.Shares.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
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
        boolean success = hubService.processPaymentWithCancellation(currentUser, request);

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

}




