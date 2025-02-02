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
import java.time.LocalDate;
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


            public List<TransactionsEntity> getTransactionsForUser(UserEntity user) {
        HubEntity userHub = user.getHub();
        return transactionsRepository.findByHub(userHub);
    }

    @Transactional
    public boolean processPaymentByHubCard(HubCardPaymentRequest request) {
        // 1) Find the hub by the provided hubCardNumber
        Optional<HubEntity> hubOptional = hubRepository.findByHubCardNumber(request.getHubCardNumber());
        if (!hubOptional.isPresent()) {
            System.out.println("Transaction failed: No hub found with the provided card number.");
            return false;
        }

        HubEntity hub = hubOptional.get();
        Double amountNeeded = request.getAmount();

        // 2) Check if there is a selected wallet in this hub
        WalletEntity selectedWallet = hub.getWallets().stream()
                .filter(WalletEntity::getSelected)
                .findFirst()
                .orElse(null);

        // -------------------------------------------------------------------------
        // CASE A: A wallet is selected -> Use existing single-wallet logic
        // -------------------------------------------------------------------------
        if (selectedWallet != null) {
            // Grab any one linked card. (You could do more sophisticated logic if there are multiple.)
            BankCardEntity linkedCard = selectedWallet.getLinkedCards().stream().findFirst().orElse(null);

            if (linkedCard == null) {
                System.out.println("Transaction canceled: no linked card on the selected wallet.");
                return false;
            }

            // Check if the selected wallet has enough balance
            if (selectedWallet.getBalance() >= amountNeeded) {
                // Deduct from wallet balance
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
                System.out.println("Transaction canceled: insufficient funds in the selected wallet.");
                return false;
            }

        } else {
            // ---------------------------------------------------------------------
            // CASE B: No wallet selected -> MULTI-CARD Checking distribution logic
            // ---------------------------------------------------------------------

            // 1) Gather all checking-type cards in the hub
            List<BankCardEntity> checkingCards = hub.getLinkedCards().stream()
                    .filter(card -> "checking".equalsIgnoreCase(card.getCardType()))
                    .collect(Collectors.toList());

            if (checkingCards.isEmpty()) {
                System.out.println("Transaction canceled: no checking cards linked to this hub.");
                return false;
            }

            // 2) Calculate total of all checking balances
            double totalCheckingBalance = checkingCards.stream()
                    .mapToDouble(BankCardEntity::getCardBalance)
                    .sum();

            // If total checking is insufficient, cancel transaction
            if (totalCheckingBalance < amountNeeded) {
                System.out.println("Transaction canceled: insufficient total checking balance.");
                return false;
            }

            // 3) Distribute amountNeeded across checking cards by ratio
            List<Double> rawShares = new ArrayList<>();
            for (BankCardEntity card : checkingCards) {
                double ratio = card.getCardBalance() / totalCheckingBalance;
                rawShares.add(ratio * amountNeeded);
            }

            // 4) Round each share to thousandths
            int[] sharesInThousandths = new int[rawShares.size()];
            int sumThousandths = 0;
            for (int i = 0; i < rawShares.size(); i++) {
                int rounded = (int) Math.round(rawShares.get(i) * 1000);
                sharesInThousandths[i] = rounded;
                sumThousandths += rounded;
            }
            int targetThousandths = (int) Math.round(amountNeeded * 1000);
            int diff = targetThousandths - sumThousandths;

            // 5) Fix any rounding difference by adjusting the largest share
            if (diff != 0) {
                int largestIndex = 0;
                for (int i = 1; i < sharesInThousandths.length; i++) {
                    if (sharesInThousandths[i] > sharesInThousandths[largestIndex]) {
                        largestIndex = i;
                    }
                }
                sharesInThousandths[largestIndex] += diff;
            }

            // 6) Deduct the final amounts from each card
            for (int i = 0; i < checkingCards.size(); i++) {
                BankCardEntity card = checkingCards.get(i);
                double finalShare = sharesInThousandths[i] / 1000.0;
                card.setCardBalance(card.getCardBalance() - finalShare);
                cardBankRepository.save(card);
            }

            // Update the hub’s aggregated balances
            hub.updateBalances();

            // 7) Create a single transaction record for the entire sum
            TransactionsEntity transaction = new TransactionsEntity();
            transaction.setTransactionName(request.getTransactionName());
            transaction.setAmount(amountNeeded);
            transaction.setHub(hub);
            transaction.setTransactionTime(LocalDateTime.now());
            // (No walletUsed here, since no wallet was selected)

            transactionsRepository.save(transaction);
            hubRepository.save(hub);

            return true;
        }
    }

    @Transactional
    public HubEntity resetHubCard(UserEntity currentUser) {
        // 1) Get the user's hub
        HubEntity hub = currentUser.getHub();
        if (hub == null) {
            return null; // user has no hub
        }
        // 2) Null out current details to force re-generation
        hub.setHubCardNumber(null);
        hub.setCvv(null);
        hub.setExpDate(null);
        // 3) Manually call the same logic used by @PrePersist / @PreUpdate
        hub.generateHubDetails();
        // 4) Save and return updated hub
        return hubRepository.save(hub);
    }
}
