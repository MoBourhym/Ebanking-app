package com.spring.ebankingbackend.web;


import com.spring.ebankingbackend.dtos.CustomerDTO;
import com.spring.ebankingbackend.entities.Customer;
import com.spring.ebankingbackend.exceptions.CustomerNotFoundException;
import com.spring.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@Data

@CrossOrigin(origins = "http://localhost:4200")
public class CustomerWebController {
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public List<CustomerDTO> customers() {
        return bankAccountService.listCustormers();
    }

    @GetMapping("/customers/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        return bankAccountService.saveCustomer(customerDTO);
    }


    @PutMapping("/customers/{customerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{customerId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCustomer(@PathVariable Long customerId) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(customerId);
    }

}
