package com.example.Shares.auth.entity;

import com.example.Shares.bankAccounts.entity.BankAccountEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class BankCardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bankName;
    private String cardNumber;
    private Double cardBalance;
    private String cardType; // e.g., Debit, Credit,saving, checking
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User
    @JsonBackReference
    private UserEntity user;
    @OneToOne
    @JoinColumn(name = "bankAccount_id", nullable = false)

    private BankAccountEntity bankAccountEntity;


    private boolean selected = false;


    // Getters and Setters


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
