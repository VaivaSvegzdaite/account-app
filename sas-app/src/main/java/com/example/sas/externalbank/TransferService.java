package com.example.sas.externalbank;

import com.example.sas.exceptions.InsufficientFundsException;
import com.example.sas.account.Account;
import com.example.sas.account.AccountRepository;
import com.example.sas.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    public void transferFunds(Transfer transfer) {
        RestTemplate restTemplate = new RestTemplate();
        String apiEndpoint = "http://www.banking-system.online/bankaccounts/api/transfer";

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(apiEndpoint)
                .queryParam("sourceAccountUsername", transfer.getSourceAccountUsername())
                .queryParam("destinationAccountUsername", transfer.getDestinationAccountUsername())
                .queryParam("amount", transfer.getAmount());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.POST,
                    request,
                    String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                updateBalanceAfterTransferLocally(transfer);
            }
            logger.info(response.getStatusCode().toString());
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
        }
    }

    private void updateBalanceAfterTransferLocally(Transfer transfer) {
        Account sourceAccount = accountRepository.findAccountByName(transfer.getSourceAccountUsername());

        if (sourceAccount != null) {
            BigDecimal amount = BigDecimal.valueOf(transfer.getAmount());

            if (sourceAccount.getBalance().compareTo(amount) >= 0) {
                sourceAccount.withdraw(amount);
                accountRepository.save(sourceAccount);
            } else {
                throw new InsufficientFundsException("Insufficient balance in the source account");
            }
        }
    }

}

