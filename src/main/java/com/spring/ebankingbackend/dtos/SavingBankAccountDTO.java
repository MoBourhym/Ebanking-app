package com.spring.ebankingbackend.dtos;


import com.spring.ebankingbackend.entities.AccountOperation;
import com.spring.ebankingbackend.entities.Customer;
import com.spring.ebankingbackend.enums.AccountStatus;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SavingBankAccountDTO extends BankAccountDTO {
    private Double interestRate;

}
