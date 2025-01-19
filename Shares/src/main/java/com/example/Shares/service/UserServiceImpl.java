package com.example.Shares.service;

import com.example.Shares.bo.CreateUserRequest;
import com.example.Shares.bo.UserResponse;
import com.example.Shares.config.JWTUtil;
import com.example.Shares.entity.BankCardEntity;
import com.example.Shares.entity.UserEntity;
import com.example.Shares.repository.BankCardRepository;
import com.example.Shares.repository.UserRepository;
import com.example.Shares.utils.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
//        this.userRepository = userRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//    }
//
//    @Override
//    public UserResponse createUser(CreateUserRequest request){
//        UserEntity userEntity = new UserEntity();
//
//        if(userRepository.existsByUsernameIgnoreCase(request.getUsername())){
//            throw new RuntimeException("Username already exists");
//        }
//
//        // Create a new UserEntity
//        userEntity.setUsername(request.getUsername());
//        userEntity.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
//        userEntity.setRole(Roles.User);
//        userEntity =userRepository.save(userEntity);
//
//        return new UserResponse(userEntity.getId(), userEntity.getUsername(),userEntity.getRole().toString());
//    }


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

    public String generateOtp(String civilId) {
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
        return otp;
    }




    public String validateOtp(String otp) {
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
        return jwtUtil.generateToken(user.getCivilId());
    }




    public void registerUser(String civilId, String username, String password) {
        UserEntity user = userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("Civil ID not found"));


        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public List<BankCardEntity> getBankCards(String token) {
        // Use the helper method to get the user
        UserEntity user = getUserFromToken(token);

        // Return all bank cards for the user
        return user.getBankCards();
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

        // Return the linked cards
        return user.getBankCards().stream()
                .filter(BankCardEntity::isSelected) // Filter where selected = true
                .collect(Collectors.toList());
    }



    public void saveSelectedCards(String token, List<Long> selectedCardIds) {
        // Extract civilId from token
        String civilId = jwtUtil.extractCivilId(token); // Ensure this method is working correctly
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
        String civilId = jwtUtil.extractCivilId(token); // Extract civilId from token
        if (civilId == null || civilId.isEmpty()) {
            throw new IllegalArgumentException("Invalid token: Civil ID not found");
        }

        return userRepository.findByCivilId(civilId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for civilId: " + civilId));
    }

}
