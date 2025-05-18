package com.spring.ebankingbackend.repositories;

import com.spring.ebankingbackend.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation,Long> {

    List<AccountOperation> findtByBankAccountId(String accountId);
    Page<AccountOperation> findByBankAccountId(String accountId, int page, int size);
}
