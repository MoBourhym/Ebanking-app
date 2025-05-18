package com.spring.ebankingbackend.services;

import com.spring.ebankingbackend.dtos.CustomerDTO;
import com.spring.ebankingbackend.entities.BankAccount;
import com.spring.ebankingbackend.entities.CurrentAccount;
import com.spring.ebankingbackend.entities.Customer;
import com.spring.ebankingbackend.entities.SavingAccount;
import com.spring.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.spring.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.spring.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentAccount saveCurrentBankAccount(Double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingAccount saveSavingBankAccount(Double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustormers();
    BankAccount getBankAccount(String accountId);
    void debit(String accountId, Double amount, String description) throws BalanceNotSufficentException;
    void credit(String accountId, Double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, Double amount) throws BankAccountNotFoundException, BalanceNotSufficentException;
     List<BankAccount> bankAccountList();
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    void deleteCustomer(Long customerId);
}
