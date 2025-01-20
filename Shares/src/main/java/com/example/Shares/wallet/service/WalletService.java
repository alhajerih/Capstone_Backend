package com.example.Shares.wallet.service;

import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.wallet.bo.CreateWalletRequest;
import com.example.Shares.wallet.entity.WalletEntity;
import com.example.Shares.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Transactional
    public void createWallet(CreateWalletRequest request, UserEntity user) {
        WalletEntity wallet = new WalletEntity();
        wallet.setName(request.getName());
        wallet.setBalance(request.getBalance());
        wallet.setHub(user.getHub());  // Associate the wallet with the user's hub

        String cardNumber = request.getCardNumber();  // Get card number from request

        BankCardEntity linkedCard = user.getHub().getLinkedCards().stream()
                .filter(card -> card.getCardNumber() != null && card.getCardNumber().equals(cardNumber))
                .findFirst()
                .orElse(null);

        if (linkedCard != null) {
            if (wallet.getLinkedCards() == null) {
                wallet.setLinkedCards(new ArrayList<>());
            }
            wallet.getLinkedCards().add(linkedCard);
        } else {
            throw new IllegalArgumentException("The provided card is not linked to the hub.");
        }

        walletRepository.save(wallet);
    }
}
