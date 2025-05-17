package com.spring.ebankingbackend;

import com.spring.ebankingbackend.entities.*;
import com.spring.ebankingbackend.enums.AccountStatus;
import com.spring.ebankingbackend.enums.OperationType;
import com.spring.ebankingbackend.exceptions.BalanceNotSufficentException;
import com.spring.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.spring.ebankingbackend.exceptions.CustomerNotFoundException;
import com.spring.ebankingbackend.repositories.AccountOperationRepository;
import com.spring.ebankingbackend.repositories.BankAccountRepository;
import com.spring.ebankingbackend.repositories.CustomerRepository;
import com.spring.ebankingbackend.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }


    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Soufian", "Mouad", "Yassine", "Hicham")
                    .forEach(name -> {
                        Customer customer = new Customer();
                        customer.setName(name);
                        customer.setEmail(name + "@gmail.com");
                        bankAccountService.saveCustomer(customer);
                    });


            bankAccountService.listCustormers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 9000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                    List<BankAccount> bankAccounts = bankAccountService.bankAccountList();
                    for (BankAccount account : bankAccounts) {
                        for (int i = 0; i < 10; i++) {
                            bankAccountService.credit(account.getId(), 1000 + Math.random() * 12000, "Credit");
                            bankAccountService.debit(account.getId(), 1000 + Math.random() * 9000, "Debit");
                        }
                    }
                } catch (CustomerNotFoundException | BankAccountNotFoundException | BalanceNotSufficentException e) {
                    e.printStackTrace();
                }
            });
        };


    }


    //  @Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository) {

        return args -> {
            // Initialize your data here if needed
            Stream.of("Soufian", "Mouad", "Yassine", "Hicham")
                    .forEach(name -> {
                        Customer customer = new Customer();
                        customer.setName(name);
                        customer.setEmail(name + "@gmail.com");
                        customerRepository.save(customer);
                    });


            customerRepository.findAll().forEach(customer -> {

                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 10000);
                currentAccount.setCreatedAt(new java.util.Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setAccountOperations(null);
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(90000.0);

                bankAccountRepository.save(currentAccount);

            });
            customerRepository.findAll().forEach(customer -> {
                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 10000);
                savingAccount.setCreatedAt(new java.util.Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setAccountOperations(null);
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5);

                bankAccountRepository.save(savingAccount);


            });

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setAmount(Math.random() * 10000);
                    accountOperation.setOperationDate(new java.util.Date());
                    accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(bankAccount);
                    accountOperationRepository.save(accountOperation);
                }
            });

        };
    }
}