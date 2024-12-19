package com.peter.bnp.kata.repository;

import com.peter.bnp.kata.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
