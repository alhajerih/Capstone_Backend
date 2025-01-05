package com.example.Company.controller;

import com.example.Company.bo.CreateUserRequest;
import com.example.Company.bo.UserResponse;
import com.example.Company.bo.auth.AuthenticationResponse;
import com.example.Company.bo.auth.CreateLoginRequest;
import com.example.Company.bo.auth.LogoutResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController
{
    private final RestTemplate restTemplate;

    public AuthController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String STOCK_API ="http://localhost:8083/api/v1/auth/";


    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateUserRequest> request = new HttpEntity<>(user, headers);
        try {
            ResponseEntity<UserResponse> responseEntity = restTemplate.postForEntity(STOCK_API+"/signup", request, UserResponse.class);
            return responseEntity;
        } catch (HttpClientErrorException e) {
            System.err.println("Error: " + e.getResponseBodyAsString());
            throw e;
        }

    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody CreateLoginRequest loginRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateLoginRequest> request = new HttpEntity<>(loginRequest, headers);

        try {
            ResponseEntity<AuthenticationResponse> responseEntity = restTemplate.postForEntity(
                    STOCK_API+"/login", request, AuthenticationResponse.class);
            return responseEntity;
        } catch (HttpClientErrorException e) {
            System.err.println("Login Error: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logoutUser(@RequestBody LogoutResponse logoutRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LogoutResponse> request = new HttpEntity<>(logoutRequest, headers);

        try {
            ResponseEntity<Void> responseEntity = restTemplate.postForEntity(
                    STOCK_API+"/logout", request, Void.class);
            return responseEntity;
        } catch (HttpClientErrorException e) {
            System.err.println("Logout Error: " + e.getResponseBodyAsString());
            throw e;
        }
    }




}
