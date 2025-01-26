package com.example.Shares.hub.service;
import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.repository.BankCardRepository;
import com.example.Shares.hub.bo.HubCardPaymentRequest;
import com.example.Shares.hub.bo.PaymentRequest;
import com.example.Shares.hub.entity.HubEntity;
import com.example.Shares.hub.repository.HubRepository;
import com.example.Shares.transactions.entity.TransactionsEntity;
import com.example.Shares.transactions.repository.TransactionsRepository;
import com.example.Shares.wallet.entity.WalletEntity;
import com.example.Shares.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
@Service
public class HubService {

    @Autowired
    private BankCardRepository cardBankRepository;

    @Autowired
    private HubRepository hubRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionsRepository transactionsRepository;

    @Transactional
    public boolean processPaymentWithCancellation(UserEntity user, PaymentRequest request) {
        HubEntity hub = user.getHub();

        // Get the selected wallet from the user's hub
        WalletEntity selectedWallet = hub.getWallets().stream()
                .filter(WalletEntity::getSelected)
                .findFirst()
                .orElse(null);

        if (selectedWallet == null) {
            System.out.println("Transaction canceled due to no selected wallet.");
            return false;
        }

        // Get the linked card from the selected wallet
        BankCardEntity linkedCard = selectedWallet.getLinkedCards().stream()
                .findFirst()
                .orElse(null);

        if (linkedCard == null) {
            System.out.println("Transaction canceled due to no linked card.");
            return false;
        }

        if (selectedWallet.getBalance() >= request.getAmount()) {
            // Deduct amount from wallet and linked card
            selectedWallet.setBalance(selectedWallet.getBalance() - request.getAmount());
            linkedCard.setCardBalance(linkedCard.getCardBalance() - request.getAmount());

            // Update the hub's total balance
            hub.updateBalances();

            // Create a new transaction record
            TransactionsEntity transaction = new TransactionsEntity();
            transaction.setTransactionName(request.getTransactionName());
            transaction.setAmount(request.getAmount());
            transaction.setWalletUsed(selectedWallet);
            transaction.setHub(hub);

            // Save transaction to the hub and wallet
            hub.addTransaction(transaction);
            selectedWallet.getTransactions().add(transaction);

            // Save the updated entities
            walletRepository.save(selectedWallet);
            cardBankRepository.save(linkedCard);
            transactionsRepository.save(transaction);
            hubRepository.save(hub);

            return true;
        }

        System.out.println("Transaction canceled due to insufficient funds in wallet.");
        return false;
    }

    public List<TransactionsEntity> getTransactionsForUser(UserEntity user) {
        HubEntity userHub = user.getHub();
        return transactionsRepository.findByHub(userHub);
    }

    @Transactional
    public boolean processPaymentByHubCard(HubCardPaymentRequest request) {
        // Find the hub by the provided hubCardNumber
        Optional<HubEntity> hubOptional = hubRepository.findByHubCardNumber(request.getHubCardNumber());

        if (!hubOptional.isPresent()) {
            System.out.println("Transaction failed: No hub found with the provided card number.");
            return false;
        }

        HubEntity hub = hubOptional.get();

        WalletEntity selectedWallet = hub.getWallets().stream()
                .filter(WalletEntity::getSelected)
                .findFirst()
                .orElse(null);

        if (selectedWallet == null) {
            System.out.println("Transaction canceled due to no selected wallet.");
            return false;
        }

        BankCardEntity linkedCard = selectedWallet.getLinkedCards().stream()
                .findFirst()
                .orElse(null);

        if (linkedCard == null) {
            System.out.println("Transaction canceled due to no linked card.");
            return false;
        }

        if (selectedWallet.getBalance() >= request.getAmount()) {
            selectedWallet.setBalance(selectedWallet.getBalance() - request.getAmount());
            linkedCard.setCardBalance(linkedCard.getCardBalance() - request.getAmount());

            // Update the hub's total balance
            hub.updateBalances();

            TransactionsEntity transaction = new TransactionsEntity();
            transaction.setTransactionName(request.getTransactionName());
            transaction.setAmount(request.getAmount());
            transaction.setWalletUsed(selectedWallet);
            transaction.setHub(hub);

            // Add transaction only to the wallet, not the hub separately
            selectedWallet.getTransactions().add(transaction);

            // Save the updated entities
            walletRepository.save(selectedWallet);
            cardBankRepository.save(linkedCard);
            transactionsRepository.save(transaction);
            hubRepository.save(hub);

            return true;
        }

        System.out.println("Transaction canceled due to insufficient funds in wallet.");
        return false;
    }
}
