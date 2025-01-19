package com.example.Shares.wallet.service;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.service.UserService;
import com.example.Shares.wallet.bo.CreateWalletRequest;
import com.example.Shares.wallet.entity.WalletEntity;
import com.example.Shares.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class WalletService {
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;


    @Transactional
    public void createWallet(CreateWalletRequest request, UserEntity user) {
        // Retrieve the currently authenticated user
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Create a new wallet and associate it with the user's hub
        WalletEntity wallet = new WalletEntity();
        wallet.setHub(user.getHub());  // Set the user's hub
        wallet.setBalance(request.getBalance());
        wallet.setName(request.getName());
        wallet.setActive(true);  // Default active status to true

        walletRepository.save(wallet);
    }
}
