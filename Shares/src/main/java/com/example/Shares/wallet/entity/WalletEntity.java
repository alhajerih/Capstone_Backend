package com.example.Shares.wallet.entity;

import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.hub.entity.HubEntity;
import com.example.Shares.transactions.entity.TransactionsEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wallets")
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double balance;
    private Boolean active;
    private Boolean selected = false; // Determines if the wallet is selected for transactions

    @ManyToOne
    @JoinColumn(name = "hub_id", referencedColumnName = "id")
    @JsonBackReference
    private HubEntity hub;

    @ManyToMany
    @JsonManagedReference
    @JoinTable(
            name = "wallets_cards",
            joinColumns = @JoinColumn(name = "wallet_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private List<BankCardEntity> linkedCards = new ArrayList<>();


    @OneToMany(mappedBy = "walletUsed", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TransactionsEntity> transactions;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public HubEntity getHub() {
        return hub;
    }

    public void setHub(HubEntity hub) {
        this.hub = hub;
    }

    public List<BankCardEntity> getLinkedCards() {
        return linkedCards;
    }

    public void setLinkedCards(List<BankCardEntity> linkedCards) {
        this.linkedCards = linkedCards;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public List<TransactionsEntity> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionsEntity> transactions) {
        this.transactions = transactions;
    }
}
