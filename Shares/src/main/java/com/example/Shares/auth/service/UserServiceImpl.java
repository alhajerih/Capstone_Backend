package com.example.Shares.auth.service;

import com.example.Shares.auth.bo.LoginResponse;
import com.example.Shares.auth.bo.otp.GenerateOtpResponse;
import com.example.Shares.auth.config.JWTUtil;
import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.auth.repository.BankCardRepository;
import com.example.Shares.auth.repository.UserRepository;
import com.example.Shares.auth.utils.Roles;
import com.example.Shares.hub.entity.HubEntity;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;


    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private TwilioService twilioService;

    public GenerateOtpResponse generateOtp(String civilId) {
        UserEntity user = userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("Civil ID not found"));


        Random random = new Random();
        String otp = String.format("%06d", random.nextInt(1000000));

        user.setOtp(otp);
        user.setRole(Roles.User);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5)); // Set expiration time
        userRepository.save(user);


        // Send OTP via Twilio
        twilioService.sendSms(user.getPhoneNumber(), otp);
        GenerateOtpResponse otpResponse = new GenerateOtpResponse();
        otpResponse.setOtp("OTP sent to registered phone number: " +otp);
        return otpResponse;
    }

    public LoginResponse validateOtp(String otp) {
        // Find the user by OTP
        UserEntity user = userRepository.findByOtp(otp);
        if (user == null) {
            throw new IllegalArgumentException("Invalid OTP");
        }

        // Check if the OTP is expired
        if (user.getOtpExpiry() == null || user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP expired");
        }

        // OTP is valid; generate a JWT token
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtUtil.generateToken(user.getCivilId()));
        return loginResponse;
    }

    public void registerUser(String civilId, String username, String password) {
        UserEntity user = userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("Civil ID not found"));


        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        HubEntity hub = new HubEntity();
        hub.setUser(user);
        user.setHub(hub);
        userRepository.save(user);
    }

    public List<BankCardEntity> getBankCards(String token) {
        // Extract civilId from the token
        String civilId = jwtUtil.extractCivilId(token);

        if (civilId == null || civilId.isEmpty()) {
            throw new IllegalArgumentException("Invalid token: Civil ID is null");
        }

        // Fetch user and ensure `bankCards` is initialized
        UserEntity user = userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Force initialization
        List<BankCardEntity> bankCards = new ArrayList<>(user.getBankCards());

        return bankCards; // This ensures Hibernate loads the collection before caching
    }




    public List<BankCardEntity> getLinkedCards(String token) {
        // Extract civilId from the token
        String civilId = jwtUtil.extractCivilId(token);

        if (civilId == null || civilId.isEmpty()) {
            throw new IllegalArgumentException("Invalid token: Civil ID not found");
        }

        // Fetch the user by civilId
        UserEntity user = userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for Civil ID: " + civilId));

        // Force initialization of bankCards to prevent lazy loading issues
        List<BankCardEntity> bankCards = new ArrayList<>(user.getBankCards());

        // Return only linked (selected) cards
        return bankCards.stream()
                .filter(BankCardEntity::isSelected) // Filter where selected = true
                .collect(Collectors.toList());
    }



    public void saveSelectedCards(String token, List<Long> selectedCardIds) {
        // Extract civilId from token
        String civilId = jwtUtil.extractCivilId(token);

        if (civilId == null || civilId.isEmpty()) {
            throw new IllegalArgumentException("Invalid token: Civil ID not found");
        }

        // Fetch the user by civilId
        UserEntity user = userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Update selected status for cards
        for (BankCardEntity card : user.getBankCards()) {
            card.setSelected(selectedCardIds.contains(card.getId()));
        }

        // Save the updated user entity
        userRepository.save(user);
    }


    public String login(String username, String password) {
        // Fetch user by username
        UserEntity user = userRepository.findByUsername(username);

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Generate JWT token
        return jwtUtil.generateToken(user.getCivilId());
    }

    //Helper function
    public UserEntity getUserFromToken(String token) {
        // Extract civilId from the token
        String civilId = jwtUtil.extractCivilId(token);

        if (civilId == null || civilId.isEmpty()) {
            throw new IllegalArgumentException("Invalid token: Civil ID not found");
        }

        // Fetch the user by civilId
        UserEntity user = userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for civilId: " + civilId));
        return user;
    }




}
