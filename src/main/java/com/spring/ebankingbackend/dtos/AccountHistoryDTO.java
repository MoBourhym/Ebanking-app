package com.spring.ebankingbackend.dtos;


import lombok.Data;

import java.util.List;

@Data
public class AccountHistoryDTO {
    private String accountId;
    private Double balance;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private List<AccountOperationDTO> accountOperationDTOS;
}
