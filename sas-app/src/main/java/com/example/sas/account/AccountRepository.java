package com.example.sas.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByName(String name);

    Page<Account> findByNameContainingIgnoreCase(String accountOwner, Pageable pageable);

    Page<Account> findById(Long id, Pageable pageable);
}
