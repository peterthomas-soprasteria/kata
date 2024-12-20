package com.peter.bnp.kata.controller;

import com.peter.bnp.kata.dto.OrderItemResponse;
import com.peter.bnp.kata.dto.OrderResponse;
import com.peter.bnp.kata.model.Order;
import com.peter.bnp.kata.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        List<Order> orderList = orderService.getOrdersForUser(username);
        return ResponseEntity.ok(toOrderResponse(orderList));
    }

    private List<OrderResponse> toOrderResponse(List<Order> orderList) {
        return orderList.stream()
                .map(order -> new OrderResponse(
                        order.getOrderItems().stream()
                                .map(orderItem -> new OrderItemResponse(
                                        orderItem.getBook().getTitle(),
                                        orderItem.getQuantity(),
                                        orderItem.getTotalPrice()
                                    )
                                ).toList(),
                        order.getTotalPrice()
                    )
                )
                .toList();
    }
}
