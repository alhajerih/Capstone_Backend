package com.example.Shares.hub.entity;

import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.savings.entity.SavingsEntity;
import com.example.Shares.wallet.entity.WalletEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hub")
public class HubEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isActive;
    private Double savingsBalance;
    private Double checkingsBalance;
    private String hubCardNumber;
    private String expDate;
    private Double cvv;
    //private WalletEntity selectedWallet;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity user;


    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<WalletEntity> wallets = new ArrayList<>();

    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<SavingsEntity> savings = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<BankCardEntity> linkedCards;

    // Getters and Setters

    public Double getSavingsBalance() {
        return savingsBalance;
    }

    public void setSavingsBalance(Double savingsBalance) {
        this.savingsBalance = savingsBalance;
    }

    public Double getCheckingsBalance() {
        return checkingsBalance;
    }

    public void setCheckingsBalance(Double checkingsBalance) {
        this.checkingsBalance = checkingsBalance;
    }

    public String getHubCardNumber() {
        return hubCardNumber;
    }

    public void setHubCardNumber(String hubCardNumber) {
        this.hubCardNumber = hubCardNumber;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public Double getCvv() {
        return cvv;
    }

    public void setCvv(Double cvv) {
        this.cvv = cvv;
    }

    public List<SavingsEntity> getSavings() {
        return savings;
    }

    public void setSavings(List<SavingsEntity> savings) {
        this.savings = savings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<WalletEntity> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletEntity> wallets) {
        this.wallets = wallets;
    }

    public List<BankCardEntity> getLinkedCards() {
        return linkedCards;
    }

    public void setLinkedCards(List<BankCardEntity> linkedCards) {
        this.linkedCards = linkedCards;
    }
}
