package com.peter.bnp.kata.exception;

public class InvalidCredentialsException extends IllegalArgumentException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
