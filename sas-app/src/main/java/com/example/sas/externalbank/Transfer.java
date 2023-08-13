package com.example.sas.externalbank;


public class Transfer {

    private String sourceAccountUsername;

    private String destinationAccountUsername;

    private double amount;

    public Transfer() {
    }

    public Transfer(String sourceAccountUsername, String destinationAccountUsername, double amount) {
        this.sourceAccountUsername = sourceAccountUsername;
        this.destinationAccountUsername = destinationAccountUsername;
        this.amount = amount;
    }

    public String getSourceAccountUsername() {
        return sourceAccountUsername;
    }

    public String getDestinationAccountUsername() {
        return destinationAccountUsername;
    }

    public double getAmount() {
        return amount;
    }


    public void setSourceAccountUsername(String sourceAccountUsername) {
        this.sourceAccountUsername = sourceAccountUsername;
    }

    public void setDestinationAccountUsername(String destinationAccountUsername) {
        this.destinationAccountUsername = destinationAccountUsername;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
