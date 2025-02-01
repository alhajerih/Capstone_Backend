package com.example.Shares.auth.entity;

import com.example.Shares.hub.entity.HubEntity;
import com.example.Shares.wallet.entity.WalletEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BankCardEntity {
    // Secure random number generator
    private static final SecureRandom RANDOM = new SecureRandom();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankName;
    private String accountNumber;
    private String cardNumber;
    private Double cardBalance;
    private String cardType; // e.g., Debit, Credit, saving, checking
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User
    @JsonBackReference
    private UserEntity user;
    @ManyToOne
    @JoinColumn(name = "hub_id") // Foreign key to HubEntity
    @JsonBackReference // Prevent infinite recursion
    private HubEntity hub;
    private boolean selected = false;
    @ManyToMany(mappedBy = "linkedCards")
    @JsonBackReference
    private List<WalletEntity> wallets = new ArrayList<>();

    // Generate account number and card number
    @PrePersist
    @PreUpdate
    public void generateAccountAndCardNumbers() {
        if (this.accountNumber == null || this.accountNumber.isEmpty()) {
            this.accountNumber = generateRandomNumber(10);
        }
        if (this.cardNumber == null || this.cardNumber.isEmpty()) {
            this.cardNumber = generateRandomNumber(16);
        }
    }

    private String generateRandomNumber(int length) {
        StringBuilder number = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            number.append(RANDOM.nextInt(10));
        }
        return number.toString();
    }

    // Getters and Setters

    public List<WalletEntity> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletEntity> wallets) {
        this.wallets = wallets;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public HubEntity getHub() {
        return hub;
    }

    public void setHub(HubEntity hub) {
        this.hub = hub;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Double getCardBalance() {
        return cardBalance;
    }

    public void setCardBalance(Double cardBalance) {
        this.cardBalance = cardBalance;
    }
}
