package com.peter.bnp.kata.dto;

import java.util.List;

public record CartResponse(List<CartItemResponse> cartItems) {
}
