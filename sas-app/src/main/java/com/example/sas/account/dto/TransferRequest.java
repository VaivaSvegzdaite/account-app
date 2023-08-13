package com.example.sas.account.dto;


public class TransferRequest extends TransactionRequest{

    private Long destinationId;

    public Long getDestinationId() {
        return destinationId;
    }

}
