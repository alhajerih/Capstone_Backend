package com.example.Shares.wallet.bo;


public class UpdateWalletRequest {

    private Long walletId;
    private String name;
    private Double balance;
    private Long cardThemeId;
    private String category;
    private Double allocation;

    public UpdateWalletRequest() {
        //Default constructor
    }

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
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

    public Double getAllocation() {
        return allocation;
    }

    public void setAllocation(Double allocation) {
        this.allocation = allocation;
    }
}
