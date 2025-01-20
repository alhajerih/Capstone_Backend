package com.example.Shares.wallet.entity;

import com.example.Shares.auth.entity.BankCardEntity;
import com.example.Shares.hub.entity.HubEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
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

    @ManyToOne
    @JoinColumn(name = "hub_id", referencedColumnName = "id")
    @JsonBackReference
    private HubEntity hub;

    @OneToMany(cascade = CascadeType.ALL)
    private List<BankCardEntity> linkedCards;


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


}
