package com.example.Shares.wallet.repository;

import com.example.Shares.wallet.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WalletRepository extends JpaRepository<WalletEntity, Long> {
//    List<BankCardEntity> findByUserId(Long userId);
}




