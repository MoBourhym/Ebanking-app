package com.spring.ebankingbackend.entities;


import com.spring.ebankingbackend.enums.AccountStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

public class BankAccount {
    @Id
    @GeneratedValue
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private Customer customer;
    private List<AccountOperation> accountOperations;
}
