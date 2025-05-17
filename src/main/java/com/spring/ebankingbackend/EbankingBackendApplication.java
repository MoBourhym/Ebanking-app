package com.spring.ebankingbackend;

import com.spring.ebankingbackend.entities.AccountOperation;
import com.spring.ebankingbackend.entities.CurrentAccount;
import com.spring.ebankingbackend.entities.Customer;
import com.spring.ebankingbackend.entities.SavingAccount;
import com.spring.ebankingbackend.enums.AccountStatus;
import com.spring.ebankingbackend.enums.OperationType;
import com.spring.ebankingbackend.repositories.AccountOperationRepository;
import com.spring.ebankingbackend.repositories.BankAccountRepository;
import com.spring.ebankingbackend.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }


    @Bean
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
                    for(int i=0; i<10; i++){
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
