package com.example.Shares.service;

import com.example.Shares.bo.CreateUserRequest;
import com.example.Shares.bo.UserResponse;
import com.example.Shares.entity.UserEntity;
import com.example.Shares.repository.UserRepository;
import com.example.Shares.utils.Roles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
private final BCryptPasswordEncoder bCryptPasswordEncoder;
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserResponse createUser(CreateUserRequest request){
        UserEntity userEntity = new UserEntity();

        if(userRepository.existsByUsernameIgnoreCase(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }

        // Create a new UserEntity
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        userEntity.setRole(Roles.User);
        userEntity =userRepository.save(userEntity);

        return new UserResponse(userEntity.getId(), userEntity.getUsername(),userEntity.getRole().toString());
    }
}
