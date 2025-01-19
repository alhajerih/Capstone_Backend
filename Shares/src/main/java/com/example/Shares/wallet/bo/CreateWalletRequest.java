package com.example.Shares.wallet.bo;

import com.example.Shares.auth.entity.BankCardEntity;

public class CreateWalletRequest {


    private String name;
    private Double balance;
    //private Boolean active;
    private BankCardEntity chosenCard;


    public CreateWalletRequest() {
        //Default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public BankCardEntity getChosenCard() {
        return chosenCard;
    }

    public void setChosenCard(BankCardEntity chosenCard) {
        this.chosenCard = chosenCard;
    }
}




