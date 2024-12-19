package com.peter.bnp.kata.dto;

public record OrderItemResponse(BookResponse book, int quantity, double totalPrice) {
}
