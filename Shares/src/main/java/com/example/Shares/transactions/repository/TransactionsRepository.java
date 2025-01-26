package com.example.Shares.transactions.repository;//package com.example.Shares.wallet.repository;

import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.hub.entity.HubEntity;
import com.example.Shares.transactions.entity.TransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TransactionsRepository extends JpaRepository<TransactionsEntity, Long> {
//    List<BankCardEntity> findByUserId(Long userId);
    List<TransactionsEntity> findByHub(HubEntity hub);


}




