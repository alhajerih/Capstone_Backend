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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public boolean processPaymentWithChecking(UserEntity user, PaymentRequest request) {
        HubEntity hub = user.getHub();
        Double amountNeeded = request.getAmount();

        // 1) Check if there is a selected wallet in this hub.
        WalletEntity selectedWallet = hub.getWallets().stream()
                .filter(WalletEntity::getSelected)
                .findFirst()
                .orElse(null);

        // -------------------------------------------------------------------------
        // CASE A: A wallet is selected -> Use existing single-wallet logic
        // -------------------------------------------------------------------------
        if (selectedWallet != null) {
            // Find the linked card for the selected wallet (assuming only one)
            BankCardEntity linkedCard = selectedWallet.getLinkedCards().stream()
                    .findFirst()
                    .orElse(null);

            if (linkedCard == null) {
                System.out.println("Transaction canceled due to no linked card on selected wallet.");
                return false;
            }

            // Check if that single wallet has enough balance
            if (selectedWallet.getBalance() >= amountNeeded) {
                // Deduct from the wallet
                selectedWallet.setBalance(selectedWallet.getBalance() - amountNeeded);
                // Deduct from the linked card
                linkedCard.setCardBalance(linkedCard.getCardBalance() - amountNeeded);

                // Update hub’s aggregated balances
                hub.updateBalances();

                // Create and save the transaction record
                TransactionsEntity transaction = new TransactionsEntity();
                transaction.setTransactionName(request.getTransactionName());
                transaction.setAmount(amountNeeded);
                transaction.setWalletUsed(selectedWallet);
                transaction.setHub(hub);
                transaction.setTransactionTime(LocalDateTime.now());
                transactionsRepository.save(transaction);

                // Persist changes
                walletRepository.save(selectedWallet);
                cardBankRepository.save(linkedCard);
                hubRepository.save(hub);

                return true;
            } else {
                System.out.println("Transaction canceled due to insufficient funds in the selected wallet.");
                return false;
            }

        } else {
            // ---------------------------------------------------------------------
            // CASE B: No wallet selected -> Use MULTI-CARD Checking distribution
            // ---------------------------------------------------------------------

            // 2) Gather all checking-type cards in the hub
            List<BankCardEntity> checkingCards = hub.getLinkedCards().stream()
                    .filter(card -> "checking".equalsIgnoreCase(card.getCardType()))
                    .collect(Collectors.toList());

            // Edge case: No checking cards at all
            if (checkingCards.isEmpty()) {
                System.out.println("Transaction canceled: No checking cards linked to Hub.");
                return false;
            }

            // 3) Calculate the total of all checking balances
            double totalCheckingBalance = checkingCards.stream()
                    .mapToDouble(BankCardEntity::getCardBalance)
                    .sum();

            // If total checking is insufficient, cancel transaction
            if (totalCheckingBalance < amountNeeded) {
                System.out.println("Transaction canceled due to insufficient total checking balance.");
                return false;
            }

            // 4) Distribute amountNeeded across checking cards by ratio
            //    (ratio = cardBalance / totalCheckingBalance)
            //    and handle rounding to 3 decimals.

            // Step A: Compute raw shares
            List<Double> rawShares = new ArrayList<>();
            for (BankCardEntity card : checkingCards) {
                double ratio = card.getCardBalance() / totalCheckingBalance;
                rawShares.add(ratio * amountNeeded);
            }

            // Step B: Round each share to 3 decimals (use integer thousandths to avoid floating errors)
            int[] sharesInThousandths = new int[rawShares.size()];
            int sumThousandths = 0;
            for (int i = 0; i < rawShares.size(); i++) {
                int rounded = (int) Math.round(rawShares.get(i) * 1000);
                sharesInThousandths[i] = rounded;
                sumThousandths += rounded;
            }

            // Step C: Compute difference vs. total target in thousandths
            int targetThousandths = (int) Math.round(amountNeeded * 1000);
            int diff = targetThousandths - sumThousandths;

            // Step D: Add or subtract any leftover difference to the largest share
            if (diff != 0) {
                // find index of the largest share
                int largestIndex = 0;
                for (int i = 1; i < sharesInThousandths.length; i++) {
                    if (sharesInThousandths[i] > sharesInThousandths[largestIndex]) {
                        largestIndex = i;
                    }
                }
                // adjust largest share by diff
                sharesInThousandths[largestIndex] += diff;
            }

            // 5) Now deduct the final amounts from each card
            for (int i = 0; i < checkingCards.size(); i++) {
                BankCardEntity card = checkingCards.get(i);
                double finalShare = sharesInThousandths[i] / 1000.0; // convert back to double
                card.setCardBalance(card.getCardBalance() - finalShare);

                // Optionally, you can also create sub-transactions per card if you wish,
                // but commonly you'd just create a single transaction record for the entire purchase.
                cardBankRepository.save(card);
            }

            // Update the hub’s aggregated balances
            hub.updateBalances();

            // 6) Create a single transaction record for the entire sum
            TransactionsEntity transaction = new TransactionsEntity();
            transaction.setTransactionName(request.getTransactionName());
            transaction.setAmount(amountNeeded);
            transaction.setHub(hub);
            transaction.setTransactionTime(LocalDateTime.now());
            // (No single wallet used, so we don’t set `walletUsed` here)

            // Save transaction and hub
            transactionsRepository.save(transaction);
            hubRepository.save(hub);

            return true;
        }
    }

//    @Transactional
//    public boolean processPaymentWithSavings(UserEntity user, PaymentRequest request) {
//        HubEntity hub = user.getHub();
//        Double amountNeeded = request.getAmount();
//
//        // 1) Check if there is a selected wallet in this hub.
//        WalletEntity selectedWallet = hub.getWallets().stream()
//                .filter(WalletEntity::getSelected)
//                .findFirst()
//                .orElse(null);
//
//        // -------------------------------------------------------------------------
//        // CASE A: A wallet is selected -> Use existing single-wallet logic
//        // -------------------------------------------------------------------------
//        if (selectedWallet != null) {
//            // Find the linked card for the selected wallet (assuming only one for simplicity)
//            BankCardEntity linkedCard = selectedWallet.getLinkedCards().stream()
//                    .findFirst()
//                    .orElse(null);
//
//            if (linkedCard == null) {
//                System.out.println("Transaction canceled due to no linked card on selected wallet.");
//                return false;
//            }
//
//            // Check if that single wallet has enough balance
//            if (selectedWallet.getBalance() >= amountNeeded) {
//                // Deduct from the wallet
//                selectedWallet.setBalance(selectedWallet.getBalance() - amountNeeded);
//                // Deduct from the linked card
//                linkedCard.setCardBalance(linkedCard.getCardBalance() - amountNeeded);
//
//                // Update hub’s aggregated balances
//                hub.updateBalances();
//
//                // Create and save the transaction record
//                TransactionsEntity transaction = new TransactionsEntity();
//                transaction.setTransactionName(request.getTransactionName());
//                transaction.setAmount(amountNeeded);
//                transaction.setWalletUsed(selectedWallet);
//                transaction.setHub(hub);
//                transaction.setTransactionTime(LocalDateTime.now());
//                transactionsRepository.save(transaction);
//
//                // Persist changes
//                walletRepository.save(selectedWallet);
//                cardBankRepository.save(linkedCard);
//                hubRepository.save(hub);
//
//                return true;
//            } else {
//                System.out.println("Transaction canceled due to insufficient funds in the selected wallet.");
//                return false;
//            }
//
//        } else {
//            // ---------------------------------------------------------------------
//            // CASE B: No wallet selected -> Use MULTI-CARD distribution (only savings cards)
//            // ---------------------------------------------------------------------
//
//            // 2) Gather only "savings"-type cards from the hub
//            List<BankCardEntity> savingsCards = hub.getLinkedCards().stream()
//                    .filter(card -> "savings".equalsIgnoreCase(card.getCardType()))
//                    .collect(Collectors.toList());
//
//            // Edge case: No savings cards at all
//            if (savingsCards.isEmpty()) {
//                System.out.println("Transaction canceled: No savings cards linked to Hub.");
//                return false;
//            }
//
//            // 3) Calculate the total balance of these savings cards
//            double totalSavingsBalance = savingsCards.stream()
//                    .mapToDouble(BankCardEntity::getCardBalance)
//                    .sum();
//
//            // If total is insufficient, cancel transaction
//            if (totalSavingsBalance < amountNeeded) {
//                System.out.println("Transaction canceled due to insufficient total savings balance.");
//                return false;
//            }
//
//            // 4) Distribute amountNeeded across savings cards by ratio:
//            //    (ratio = cardBalance / totalSavingsBalance), with rounding to 3 decimals.
//
//            // Step A: Compute raw shares
//            List<Double> rawShares = new ArrayList<>();
//            for (BankCardEntity card : savingsCards) {
//                double ratio = card.getCardBalance() / totalSavingsBalance;
//                rawShares.add(ratio * amountNeeded);
//            }
//
//            // Step B: Round each share to 3 decimals (using integer thousandths to avoid floating errors)
//            int[] sharesInThousandths = new int[rawShares.size()];
//            int sumThousandths = 0;
//            for (int i = 0; i < rawShares.size(); i++) {
//                int rounded = (int) Math.round(rawShares.get(i) * 1000);
//                sharesInThousandths[i] = rounded;
//                sumThousandths += rounded;
//            }
//
//            // Step C: Compute difference vs. total target in thousandths
//            int targetThousandths = (int) Math.round(amountNeeded * 1000);
//            int diff = targetThousandths - sumThousandths;
//
//            // Step D: Add or subtract any leftover difference to/from the largest share
//            if (diff != 0) {
//                // find index of the largest share in sharesInThousandths
//                int largestIndex = 0;
//                for (int i = 1; i < sharesInThousandths.length; i++) {
//                    if (sharesInThousandths[i] > sharesInThousandths[largestIndex]) {
//                        largestIndex = i;
//                    }
//                }
//                // adjust largest share by diff
//                sharesInThousandths[largestIndex] += diff;
//            }
//
//            // 5) Deduct the final amounts from each savings card
//            for (int i = 0; i < savingsCards.size(); i++) {
//                BankCardEntity card = savingsCards.get(i);
//                double finalShare = sharesInThousandths[i] / 1000.0; // convert thousandths back to a double
//                card.setCardBalance(card.getCardBalance() - finalShare);
//
//                cardBankRepository.save(card);
//            }
//
//            // Update the hub’s aggregated balances
//            hub.updateBalances();
//
//            // 6) Create a single transaction record for the entire amount
//            TransactionsEntity transaction = new TransactionsEntity();
//            transaction.setTransactionName(requ
//
//
//
//
//
//


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
            transaction.setTransactionTime(LocalDateTime.now());

            // Save transaction only once
            transactionsRepository.save(transaction);

            // Save the updated entities
            walletRepository.save(selectedWallet);
            cardBankRepository.save(linkedCard);
            hubRepository.save(hub);

            return true;
        }

        System.out.println("Transaction canceled due to insufficient funds in wallet.");
        return false;
    }
}
