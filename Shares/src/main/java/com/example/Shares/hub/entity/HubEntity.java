package com.example.Shares.hub.entity;

import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.auth.entity.UserEntity;
import com.example.Shares.transactions.entity.TransactionsEntity;
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
    private String cvv;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<WalletEntity> wallets = new ArrayList<>();



    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<TransactionsEntity> transactions = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    private List<BankCardEntity> linkedCards;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

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

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
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

    public List<TransactionsEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionsEntity> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(TransactionsEntity transaction) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(transaction);
    }
}
