package com.example.Shares.wallet.bo;

public class CreateWalletRequest {


    private String name;
    private Double balance;
    private String cardNumber;  // New field for card number
    private Long cardThemeId;
    private String category;

    public CreateWalletRequest() {
        //Default constructor
    }

    public Long getCardThemeId() {
        return cardThemeId;
    }

    public void setCardThemeId(Long cardThemeId) {
        this.cardThemeId = cardThemeId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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




