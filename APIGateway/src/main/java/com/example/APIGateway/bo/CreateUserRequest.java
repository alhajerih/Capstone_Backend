package com.example.Company.bo;

public class CreateUserRequest {

    private String username;
    private String password;

    public CreateUserRequest() {
        //Default constructor
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
