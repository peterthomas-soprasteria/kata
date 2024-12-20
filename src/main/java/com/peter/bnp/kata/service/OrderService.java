package com.peter.bnp.kata.service;

import com.peter.bnp.kata.exception.UserNotFoundException;
import com.peter.bnp.kata.model.Order;
import com.peter.bnp.kata.model.User;
import com.peter.bnp.kata.repository.OrderRepository;
import com.peter.bnp.kata.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getOrdersForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return orderRepository.findByUser(user);
    }
}
