package com.example.Shares.transactions.service;

import com.example.Shares.transactions.entity.TransactionsEntity;
import com.example.Shares.transactions.repository.TransactionsRepository;
import com.example.Shares.wallet.entity.WalletEntity;
import com.example.Shares.wallet.repository.WalletRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransactionService {

    private final TransactionsRepository transactionRepository;
    private final WalletRepository walletRepository;
    public TransactionService(TransactionsRepository transactionRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    public Page<TransactionsEntity> getTransactionsByWalletId(Long walletId, Pageable pageable) {
        WalletEntity wallet = walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet not found"));
        return transactionRepository.findByWalletUsed(wallet, pageable);
    }
}
