package com.example.Shares.wallet.bo;

public class CreateWalletRequest {


    private String name;
    private Double balance;
    //private Boolean active;
//    private BankCardEntity chosenCard;
    private String cardNumber;  // New field for card number

    public CreateWalletRequest() {
        //Default constructor
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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


}




