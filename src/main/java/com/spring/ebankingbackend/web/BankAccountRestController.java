package com.spring.ebankingbackend.web;

import com.spring.ebankingbackend.dtos.AccountHistoryDTO;
import com.spring.ebankingbackend.dtos.AccountOperationDTO;
import com.spring.ebankingbackend.dtos.BankAccountDTO;
import com.spring.ebankingbackend.exceptions.BankAccountNotFoundException;
import com.spring.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController

@Slf4j
@Data
@CrossOrigin(origins = "http://localhost:4200")
public class BankAccountRestController {
    private BankAccountService bankAccountService;
    public BankAccountRestController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }


    @GetMapping("/accounts/{accountId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }

@GetMapping("/accounts")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public List<BankAccountDTO> listAccounts() {
        return bankAccountService.bankAccountList();
    }


    @GetMapping("/accounts/{accountId}/operations")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public List<AccountOperationDTO> getHistory(
            @PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{accountId}/pageOperations")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public AccountHistoryDTO getHistory(@PathVariable String accountId, @RequestParam(name = "page")int page, @RequestParam(name="size")int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }
}
