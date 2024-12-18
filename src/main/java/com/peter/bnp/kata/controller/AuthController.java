package com.peter.bnp.kata.controller;

import com.peter.bnp.kata.dto.UserRegistrationRequest;
import com.peter.bnp.kata.dto.UserReponse;
import com.peter.bnp.kata.model.User;
import com.peter.bnp.kata.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = authService.registerUser(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserReponse(user.getUsername()));
    }

}
