package com.example.Shares.hub.service;


import com.example.Shares.auth.bo.BankCardRequest;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.repository.BankCardRepository;
import com.example.Shares.hub.repository.HubRepository;
import com.example.Shares.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HubService {

    @Autowired
    private BankCardRepository cardBankRepository;

    @Autowired
    private HubRepository hubRepository;
    @Autowired
    private WalletRepository walletRepository;

    public void addCardToWallet(BankCardRequest request, UserEntity user) {

    }


}

