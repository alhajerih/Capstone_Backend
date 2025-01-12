package com.example.Shares.repository;

import com.example.Shares.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
//    Optional<UserEntity> findByUsername(String username);

//    UserEntity findByCivilId(String civilId);
    UserEntity findByUsername(String username);
    UserEntity findByOtp(String otp);
    Optional<UserEntity> findByCivilId(String civilId);
}
