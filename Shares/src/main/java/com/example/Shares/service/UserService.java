package com.example.Shares.service;

import com.example.Shares.bo.CreateUserRequest;
import com.example.Shares.bo.UserResponse;
import com.example.Shares.entity.BankCardEntity;
import com.example.Shares.entity.UserEntity;

import java.util.List;

public interface UserService {
//    UserResponse createUser(CreateUserRequest request);
List<BankCardEntity> getBankCards(String civilId);
    void registerUser(String civilId, String username, String password);
    String validateOtp( String otp);
    String generateOtp(String civilId);
    void saveSelectedCards(String token, List<Long> selectedCardIds);
    List<BankCardEntity> getLinkedCards(String token);
    UserEntity getUserFromToken(String token);
}
