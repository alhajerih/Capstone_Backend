package com.example.Shares.auth.service;

import com.example.Shares.auth.bo.LoginResponse;
import com.example.Shares.auth.bo.otp.GenerateOtpResponse;
import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<BankCardEntity> getBankCards(String civilId);

    LoginResponse registerUser(String civilId, String username, String password);

    String validateOtp(String otp);

    GenerateOtpResponse generateOtp(String civilId);

    void saveSelectedCards(String token, List<Long> selectedCardIds);

    List<BankCardEntity> getLinkedCards(String token);

    UserEntity getUserFromToken(String token);

    String login(String username, String password);
}


