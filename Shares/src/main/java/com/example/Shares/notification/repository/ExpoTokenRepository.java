//package com.example.Shares.notification.repository;
//
//
//import com.example.Shares.notification.entity.ExpoToken;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface ExpoTokenRepository extends JpaRepository<ExpoToken,Long> {
//    Optional<ExpoToken> findByToken(String token);
//    Optional<ExpoToken> findTopByOrderByIdDesc();
//    Optional<ExpoToken>findByUserId(Long userId);
//}
