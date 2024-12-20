package com.peter.bnp.kata.repository;

import com.peter.bnp.kata.model.Order;
import com.peter.bnp.kata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
