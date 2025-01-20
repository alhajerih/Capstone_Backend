package com.example.Shares.hub.repository;//package com.example.Shares.wallet.repository;

import com.example.Shares.hub.entity.HubEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface HubRepository extends JpaRepository<HubEntity, Long> {
//    List<BankCardEntity> findByUserId(Long userId);
}




