package com.example.Shares.hub.bo;

public class HubCardPaymentRequest {
    private String hubCardNumber;
    private Double amount;
    private String transactionName;


    // Getters and Setters
    public String getHubCardNumber() {
        return hubCardNumber;
    }

    public void setHubCardNumber(String hubCardNumber) {
        this.hubCardNumber = hubCardNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getTransactionName() {
        return transactionName;
    }

    public void setTransactionName(String transactionName) {
        this.transactionName = transactionName;
    }

    @Override
    public String toString() {
        return "HubCardPaymentRequest{" +
                "hubCardNumber='" + hubCardNumber + '\'' +
                ", amount=" + amount +
                ", transactionName='" + transactionName + '\'' +
                '}';
    }
}
