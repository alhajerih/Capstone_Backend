package com.example.Shares.controller;

import com.example.Shares.bo.CreateUserRequest;
import com.example.Shares.bo.UserResponse;
import com.example.Shares.bo.auth.AuthenticationResponse;
import com.example.Shares.bo.auth.CreateLoginRequest;
import com.example.Shares.bo.auth.LogoutResponse;
import com.example.Shares.service.UserService;
import com.example.Shares.service.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            System.out.println("Incoming Request: " + request);
            UserResponse response = userService.createUser(request);
            if (response != null) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                System.out.println("User creation failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }



    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody CreateLoginRequest authenticationRequest) {
        try {
            AuthenticationResponse authenticationResponse = authService.login(authenticationRequest);
            HttpStatus status = HttpStatus.OK;

            if (authenticationResponse == null) {
                status = HttpStatus.BAD_REQUEST;
            }

            return new ResponseEntity<>(authenticationResponse, status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutResponse auntenticationRequset) {
        authService.logout(auntenticationRequset);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
