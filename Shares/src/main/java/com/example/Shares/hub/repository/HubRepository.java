package com.example.Shares.hub.repository;//package com.example.Shares.wallet.repository;

import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.hub.entity.HubEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface HubRepository extends JpaRepository<HubEntity, Long> {
    //    List<BankCardEntity> findByUserId(Long userId);
    default Optional<HubEntity> findByHubCardNumber(String hubCardNumber) {
        return null;
    }

    Optional<HubEntity> findFirstByOrderByIdAsc();

}




