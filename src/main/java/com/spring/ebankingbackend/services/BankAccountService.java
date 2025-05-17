package com.spring.ebankingbackend.services;

import com.spring.ebankingbackend.entities.BankAccount;
import com.spring.ebankingbackend.entities.CurrentAccount;
import com.spring.ebankingbackend.entities.Customer;
import com.spring.ebankingbackend.entities.SavingAccount;
import com.spring.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.spring.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.spring.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    Customer saveCustomer(Customer customer);
    CurrentAccount saveCurrentBankAccount(Double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(Double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    List<Customer> listCustormers();
    BankAccount getBankAccount(String accountId);
    void debit(String accountId, Double amount, String description) throws BalanceNotSufficentException;
    void credit(String accountId, Double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, Double amount) throws BankAccountNotFoundException, BalanceNotSufficentException;
     List<BankAccount> bankAccountList();
}
