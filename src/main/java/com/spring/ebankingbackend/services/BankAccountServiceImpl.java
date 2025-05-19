package com.spring.ebankingbackend.services;

import com.spring.ebankingbackend.dtos.*;
import com.spring.ebankingbackend.entities.*;
import com.spring.ebankingbackend.enums.OperationType;
import com.spring.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.spring.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.spring.ebankingbackend.exceptions.CustomerNotFoundException;
import com.spring.ebankingbackend.mappers.BankAccountMapperImpl;
import com.spring.ebankingbackend.repositories.AccountOperationRepository;
import com.spring.ebankingbackend.repositories.BankAccountRepository;
import com.spring.ebankingbackend.repositories.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
@Transactional

@Data
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountMapperImpl bankAccountMapperImpl;
    private BankAccountRepository bankAccountRepository;
    private CustomerRepository customerRepository;
    private AccountOperationRepository accountOperationRepository;

    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving Customer " + customerDTO);
        Customer customer = bankAccountMapperImpl.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(Double initialBalance, double intrestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        SavingAccount savingAccount;
        savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new java.util.Date());
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(intrestRate);
        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);

        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(Double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);

        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }
        CurrentAccount currentAccount;
        currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreatedAt(new java.util.Date());
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);
    }

    @Override
    public List<CustomerDTO> listCustormers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Bank account not found"));

        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, Double amount, String description) throws BalanceNotSufficentException, BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
        if (bankAccount.getBalance() < amount) {
            throw new BalanceNotSufficentException("Insufficient balance");
        }
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationDate(new java.util.Date());
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void credit(String accountId, Double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(() -> new BankAccountNotFoundException("Bank account not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setOperationDate(new java.util.Date());
        accountOperation.setAmount(amount);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setDescription(description);
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, Double amount) throws BankAccountNotFoundException, BalanceNotSufficentException {
        debit(accountIdSource, amount, "Transfer to " + accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from " + accountIdSource);

    }


    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccont -> {

            if (bankAccont instanceof SavingAccount) {
                return dtoMapper.fromSavingBankAccount((SavingAccount) bankAccont);
            } else {
                return dtoMapper.fromCurrentBankAccount((CurrentAccount) bankAccont);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        log.info("Get customer with ID: {}", customerId);
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Update customer with ID: {}", customerDTO.getId());
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return bankAccountMapperImpl.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        log.info("Delete customer with ID: {}", customerId);
        customerRepository.deleteById(customerId);
    }


    @Override
    public List<AccountOperationDTO> accountHistory(String accountId) {
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId);
        return accountOperations.stream().map((op) -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null){throw new BankAccountNotFoundException("Bank account not found");}
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountIdOrderByOperationDateDesc(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map((op) -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
       accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
       accountHistoryDTO.setPageSize(size);
       accountHistoryDTO.setCurrentPage(page);

        accountHistoryDTO.setPageSize(accountOperations.getSize());

        return accountHistoryDTO;
    }

}

