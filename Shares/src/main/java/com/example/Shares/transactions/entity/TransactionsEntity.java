//package com.example.Shares.transactions.entity;
//
//import com.example.Shares.auth.entity.BankCardEntity;
//
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import java.util.List;
//
//
//@Entity
//public class TransactionsEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String name;
//    private String amount;
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
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getAmount() {
//        return amount;
//    }
//
//    public void setAmount(String amount) {
//        this.amount = amount;
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
