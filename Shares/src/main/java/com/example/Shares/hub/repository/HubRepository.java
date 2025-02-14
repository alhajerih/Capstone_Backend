package com.example.Shares.hub.repository;//package com.example.Shares.wallet.repository;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.hub.entity.HubEntity;
import com.example.Shares.transactions.entity.TransactionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface HubRepository extends JpaRepository<HubEntity, Long> {
    //    List<BankCardEntity> findByUserId(Long userId);
    Optional<HubEntity> findByHubCardNumber(String hubCardNumber);
    Optional<HubEntity> findFirstByOrderByIdAsc();
    // Find HubEntity by UserEntity
    HubEntity findByUser(UserEntity user);

}




