package com.example.Shares.service;

import com.example.Shares.bo.CreateUserRequest;
import com.example.Shares.bo.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);

}
