package com.example.sas.account;


import com.example.sas.exceptions.InsufficientFundsException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "accounts")
public class Account implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(Account.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    @NotBlank(message = "{account.name.notBlank}")
    @NotEmpty(message = "{account.name.notEmpty}")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "{account.name.pattern}")
    private String name;

    @Column
    @NotNull(message = "{account.balance.notNull}")
    @DecimalMin(value = "0.0", inclusive = false, message = "{account.balance.decimalMin}")
    @Digits(integer = 8, fraction = 2, message = "{account.balance.digits}")
    private BigDecimal balance;

    public Account() {
    }

    public Account(String name) {
        this.name = name;
        this.balance = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getBalance() {
        logger.info("Account balance is {}", balance);
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void deposit(BigDecimal amount) {

        if (amount != null) {
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                balance = balance.add(amount);
                logger.info("Successful account deposit by {} amount. Total balance: {} ", amount, balance);
            } else {
                logger.error("Failed to deposit account by negative amount {} !", amount);
            }
        } else {
            throw new RuntimeException("Deposit amount: " + amount + " not valid");
        }
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0
                && balance.subtract(amount).compareTo(BigDecimal.ZERO) >= 0) {
            balance = balance.subtract(amount);
            logger.info("Successful account withdraw by: {} amount. Total balance: {}", amount, balance);
        } else {
            logger.error("Failed to withdraw from account amount of {}! Insufficient funds!", amount);
        }
    }

    public synchronized void transfer(Account destination, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount to transfer must be positive!");
        }
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds!");
        }
        balance = balance.subtract(amount);
        destination.balance = destination.balance.add(amount);
        logger.info("Transfer of {} amount was successful!", amount);
    }

}
