package com.example.sas.module;


import com.example.sas.account.Account;
import com.example.sas.exceptions.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Account account;

    private Account destinationAccount;

    @BeforeEach
    public void setUp() {
        account = new Account("Tom Tomas");
        destinationAccount = new Account("Pi Pinas");
    }

    @Test
    @DisplayName("Test account positive balance")
    public void testAccountPositiveBalance() {
        account.setBalance(new BigDecimal(100));
        assertEquals(new BigDecimal(100), account.getBalance());
    }

    @Test
    @DisplayName("Test account negative balance")
    public void testAccountNegativeBalance() {
        account.setBalance(new BigDecimal(-50));
        assertEquals(new BigDecimal(-50), account.getBalance());
    }

    @Test
    @DisplayName("Test account negative balance")
    public void testAccountZeroBalance() {
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    @DisplayName("Test account deposit")
    public void deposit100_DepositMethod_ShouldIncreaseBalanceBy100() {
        account.deposit(new BigDecimal("100"));
        assertEquals(new BigDecimal("100"), account.getBalance());
    }

    @Test
    @DisplayName("Test account negative deposit")
    public void deposit100_DepositMethod_ShouldFailDueNegativeAmount() {
        account.deposit(new BigDecimal("-100"));
        assertEquals(new BigDecimal("0"), account.getBalance());
    }

    @Test
    @DisplayName("Test successful withdraw")
    public void withdraw50_WithdrawMethod_ShouldDecreaseBalanceBy50() {
        account.setBalance(new BigDecimal(100));
        account.withdraw(new BigDecimal("50"));
        assertEquals(new BigDecimal("50"), account.getBalance());
    }

    @Test
    @DisplayName("Test account withdraw insufficient funds")
    public void withdraw150_WithdrawMethod_ShouldFailDueToInsufficientFunds() {
        account.setBalance(new BigDecimal(100));
        account.withdraw(new BigDecimal("150"));
        assertEquals(new BigDecimal("100"), account.getBalance());
    }

    @Test
    @DisplayName("Test successful transfer between accounts")
    public void transfer100_TransferMethod_ShouldDecreaseBalanceBy100AndIncreaseDestinationBalanceBy100() {
        account.setBalance(new BigDecimal(500));
        account.transfer(destinationAccount, new BigDecimal(100));
        assertEquals(new BigDecimal(400), account.getBalance());
        assertEquals(new BigDecimal(100), destinationAccount.getBalance());
    }

    @Test
    @DisplayName("Test failed transfer due to insufficient funds")
    public void transfer1000_TransferMethod_ShouldThrowInsufficientFundsException() {
        account.setBalance(new BigDecimal(500));
        assertThrows(InsufficientFundsException.class, () -> {
            account.transfer(destinationAccount, new BigDecimal(1000));
        });
    }

    @Test
    @DisplayName("Test failed transfer negative amount")
    public void transferNegative100_TransferMethod_ShouldThrowIllegalArgumentException() {
        account.setBalance(new BigDecimal(500));
        assertThrows(IllegalArgumentException.class, () -> {
            account.transfer(destinationAccount, new BigDecimal(-100));
        });
    }

    @Test
    @DisplayName("Test check balance")
    public void checkBalance_GetBalanceMethod_ShouldReturnBalance200() {
        account.setBalance(new BigDecimal(200));
        assertEquals(new BigDecimal(200), account.getBalance());
    }

}