package com.example.Shares.service.auth;


import com.example.Shares.bo.CustomUserDetails;
import com.example.Shares.entity.UserEntity;
import com.example.Shares.exception.UserNotFoundException;
import com.example.Shares.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return buildCustomUserDetailsOfUsername(username);
    }

    private CustomUserDetails buildCustomUserDetailsOfUsername(String username) {
        UserEntity user =
                userRepository
                        .findByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new UserNotFoundException("Incorrect Username"));

        CustomUserDetails userDetails = new CustomUserDetails();
        userDetails.setId(user.getId());
        userDetails.setUserName(user.getUsername());
        userDetails.setPassword(user.getPassword());
        userDetails.setRole(user.getRole().toString());


        return userDetails;
    }
}