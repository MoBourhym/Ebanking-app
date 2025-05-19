package com.spring.ebankingbackend.repositories;

import com.spring.ebankingbackend.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
