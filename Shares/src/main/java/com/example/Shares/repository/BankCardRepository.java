package com.example.Shares.repository;

import com.example.Shares.entity.BankCardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankCardRepository extends JpaRepository<BankCardEntity,Long> {
    List<BankCardEntity> findByUserId(Long userId);
}
