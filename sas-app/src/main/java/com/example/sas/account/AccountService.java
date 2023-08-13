package com.example.sas.account;

import com.example.sas.exceptions.NotFoundAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public Optional<Account> getAccountById(Long id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            return optionalAccount;
        } else {
            logger.error("Failed to retrieve account by ID: {}", id);
            return Optional.empty();
        }
    }

    @Transactional
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Transactional
    public void updateAccount(Long id, Account account) {
        if (accountRepository.findById(id).isPresent()) {
            Account acc = accountRepository.findById(id).get();
            acc.setName(account.getName());
            acc.setBalance(account.getBalance());
            accountRepository.save(acc);
        }
    }

    @Transactional
    public void deleteAccount(Long id) {
        accountRepository.deleteById(id);
    }


    @Transactional
    public void depositToAccount(Long id, BigDecimal amount) {
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.deposit(amount);
            accountRepository.save(account);
        } else {
            logger.error("Failed to deposit to account. Account with ID {} not found.", id);
            throw new NotFoundAccountException("Account not found");
        }
    }

    @Transactional
    public void withdrawFromAccount(Long id, BigDecimal amount) {
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.withdraw(amount);
            accountRepository.save(account);
        } else {
            logger.error("Failed to withdraw from account. Account with ID {} not found.", id);
            throw new NotFoundAccountException("Account not found");
        }
    }

    @Transactional
    public void transferBetweenAccounts(Long sourceAccountId, Long destinationAccountId, BigDecimal amount) {
        Optional<Account> optionalSourceAccount = accountRepository.findById(sourceAccountId);
        Optional<Account> optionalDestinationAccount = accountRepository.findById(destinationAccountId);

        if (optionalSourceAccount.isPresent() && optionalDestinationAccount.isPresent()) {
            Account sourceAccount = optionalSourceAccount.get();
            Account destinationAccount = optionalDestinationAccount.get();

            sourceAccount.transfer(destinationAccount, amount);

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);
        } else {
            logger.error("Failed to perform transfer. Source or destination account not found.");
            throw new NotFoundAccountException("Account not found");
        }
    }

    @Transactional
    public Page<Account> findAll(Pageable pageable) {
        return accountRepository.findAll(pageable);
    }

    public Page<Account> searchByName(String name, Pageable paging) {
        return accountRepository.findByNameContainingIgnoreCase(name, paging);
    }
}
