package com.spring.ebankingbackend.security.web;

import com.spring.ebankingbackend.security.dto.AuthenticationRequest;
import com.spring.ebankingbackend.security.dto.AuthenticationResponse;
import com.spring.ebankingbackend.security.dto.RegisterRequest;
import com.spring.ebankingbackend.security.service.AuthenticationService;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        authenticationService.register(request);
        return ResponseEntity.ok(Map.of(
        "message", "User registered successfully",
        "username", request.getUsername()
    ));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
