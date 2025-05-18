package com.spring.ebankingbackend.dtos;

import com.spring.ebankingbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;


@Data
public class CurrentBankAccountDTO extends BankAccountDTO {


    private Double overDraft;
}
