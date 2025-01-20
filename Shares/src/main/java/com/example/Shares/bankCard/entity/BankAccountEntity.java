//package com.example.Shares.bankAccounts.entity;//package com.example.Shares.transactions.entity;
//
//import com.example.Shares.auth.entity.BankCardEntity;
//import com.example.Shares.auth.entity.UserEntity;
//import com.fasterxml.jackson.annotation.JsonBackReference;
//
//import javax.persistence.*;
//import java.util.List;
//
//
//@Entity
//public class BankAccountEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String bankName;
//
//    @OneToOne
//    @JoinColumn(name = "bankCard_id", nullable = false)
//    private BankCardEntity bankCard;
//    @ManyToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    @JsonBackReference
//    private UserEntity user;
//
//    private Boolean isActive;
//    private List<BankCardEntity> linkedCards; // e.g., Debit, Credit,saving, checking
/// /   private List<TransactionEntity> transactions;
//
////    @ManyToOne
////    @JoinColumn(name = "user_id", nullable = false) // Foreign key to User
////    @JsonBackReference
////    private UserEntity user;
//
//
//    // Getters and Setters
//
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getBankName() {
//        return bankName;
//    }
//
//    public void setBankName(String bankName) {
//        this.bankName = bankName;
//    }
//
//    public BankCardEntity getBankCard() {
//        return bankCard;
//    }
//
//    public void setBankCard(BankCardEntity bankCard) {
//        this.bankCard = bankCard;
//    }
//
//    public UserEntity getUser() {
//        return user;
//    }
//
//    public void setUser(UserEntity user) {
//        this.user = user;
//    }
//
//    public Boolean getActive() {
//        return isActive;
//    }
//
//    public void setActive(Boolean active) {
//        isActive = active;
//    }
//
//    public List<BankCardEntity> getLinkedCards() {
//        return linkedCards;
//    }
//
//    public void setLinkedCards(List<BankCardEntity> linkedCards) {
//        this.linkedCards = linkedCards;
//    }
//}
//
//
