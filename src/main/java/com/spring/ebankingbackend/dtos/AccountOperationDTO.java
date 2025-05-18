package com.spring.ebankingbackend.dtos;

import com.spring.ebankingbackend.entities.BankAccount;
import com.spring.ebankingbackend.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data


public class AccountOperationDTO {

    private Long id;
    private Date operationDate;
    private Double amount;
    private String description;
    private OperationType type;
    private BankAccount bankAccount;


}
