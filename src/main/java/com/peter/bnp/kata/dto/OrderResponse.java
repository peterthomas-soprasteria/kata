package com.peter.bnp.kata.dto;

import java.util.List;

public record OrderResponse(List<OrderItemResponse> orderItems, double totalPrice) {
}
