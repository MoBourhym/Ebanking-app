package com.spring.ebankingbackend.services;

import com.spring.ebankingbackend.dtos.*;
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
    CurrentBankAccountDTO saveCurrentBankAccount(Double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(Double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    List<CustomerDTO> listCustormers();
    BankAccountDTO getBankAccount(String accountId);
    void debit(String accountId, Double amount, String description) throws BalanceNotSufficentException, BankAccountNotFoundException;
    void credit(String accountId, Double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, Double amount) throws BankAccountNotFoundException, BalanceNotSufficentException;
    List<BankAccountDTO> bankAccountList();
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    void deleteCustomer(Long customerId);
    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
