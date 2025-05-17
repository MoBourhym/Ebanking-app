package com.spring.ebankingbackend.web;


import com.spring.ebankingbackend.entities.Customer;
import com.spring.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Data
public class CustomerWebController {
    private BankAccountService bankAccountService;
    @GetMapping("/customers")
    public List<Customer> customers() {
        return  bankAccountService.listCustormers();
    }
}
