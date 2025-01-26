package com.example.Shares.hub.bo;

public class PaymentRequest {
    private double amount;
    private String transactionName;

    // Constructors
    public PaymentRequest() {}

    public PaymentRequest(double amount, String transactionName) {
        this.amount = amount;
        this.transactionName = transactionName;
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }
}
