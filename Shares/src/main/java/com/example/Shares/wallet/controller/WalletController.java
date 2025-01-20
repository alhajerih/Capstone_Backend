package com.example.Shares.wallet.controller;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.service.UserService;
import com.example.Shares.wallet.bo.CreateWalletRequest;
import com.example.Shares.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class WalletController {


    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;

    @PostMapping("/create-wallet")
    public ResponseEntity<String> createNewWallet(@RequestHeader("Authorization") String token, @RequestBody CreateWalletRequest request) {

        String jwt = token.substring(7); // Remove "Bearer " prefix
        UserEntity user = userService.getUserFromToken(jwt);

        walletService.createWallet(request, user);
        return ResponseEntity.ok("Wallet created");
    }


}


