package com.example.Shares.wallet.controller;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.service.UserService;
import com.example.Shares.wallet.bo.CreateWalletRequest;
import com.example.Shares.wallet.bo.UpdateWalletRequest;
import com.example.Shares.wallet.entity.WalletEntity;
import com.example.Shares.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    @PostMapping("/select-wallet")
    public ResponseEntity<String> selectWallet(@RequestHeader("Authorization") String token, @RequestBody Map<String, Long> requestBody) {
        String jwt = token.substring(7);
        UserEntity user = userService.getUserFromToken(jwt);

        Long walletId = requestBody.get("walletId");
        if (walletId == null) {
            return ResponseEntity.badRequest().body("Wallet ID is required.");
        }

        walletService.selectWallet(user, walletId);
        return ResponseEntity.ok("Wallet selected successfully");
    }

    @PostMapping("/deselect-wallet")
    public ResponseEntity<String> deselectAllWallets(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);

        UserEntity user = userService.getUserFromToken(jwt);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid token or user not found.");
        }

        walletService.deselectAllWallets(user);

        return ResponseEntity.ok("All wallets have been deselected successfully.");
    }

    @GetMapping("/view-all-wallets")
    public ResponseEntity<List<WalletEntity>> getAllWallets(
            @RequestHeader("Authorization") String token
    ) {
        String jwt = token.substring(7);

        UserEntity currentUser = userService.getUserFromToken(jwt);
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        List<WalletEntity> wallets = walletService.getAllWalletsForUser(currentUser);

        return ResponseEntity.ok(wallets);
    }

    @PostMapping("/update-wallet")
    public ResponseEntity<String> updateWallet(@RequestHeader("Authorization") String token, @RequestBody UpdateWalletRequest request) {
        String jwt = token.substring(7);
        UserEntity user = userService.getUserFromToken(jwt);

        walletService.updateWallet(user, request);
        return ResponseEntity.ok("Wallet updated successfully");
    }


    @PostMapping("/delete-wallet")
    public ResponseEntity<String> deleteWallet(@RequestHeader("Authorization") String token, @RequestBody Map<String, Long> requestBody) {
        String jwt = token.substring(7);
        UserEntity user = userService.getUserFromToken(jwt);

        Long walletId = requestBody.get("walletId");
        if (walletId == null) {
            return ResponseEntity.badRequest().body("Wallet ID is required.");
        }

        walletService.deleteWallet(user, walletId);
        return ResponseEntity.ok("Wallet deleted successfully");
    }
    @PostMapping("/toggle-active")
    public ResponseEntity<String> toggleWalletActiveStatus(@RequestHeader("Authorization") String token,
                                                           @RequestBody UpdateWalletRequest request) {
        String jwt = token.substring(7);

        UserEntity user = userService.getUserFromToken(jwt);

        walletService.toggleWalletActiveStatus(user, request.getWalletId());

        return ResponseEntity.ok("Wallet active status toggled successfully.");
    }


}


