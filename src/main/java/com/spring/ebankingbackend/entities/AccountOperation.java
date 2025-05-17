package com.spring.ebankingbackend.entities;

import com.spring.ebankingbackend.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data @NoArgsConstructor @AllArgsConstructor
public class AccountOperation {

    private Long id;
    private Date operationDate;
    private Double amount;
    private OperationType type;
    private BankAccount bankAccount;

}
