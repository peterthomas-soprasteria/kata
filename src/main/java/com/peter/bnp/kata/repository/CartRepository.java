package com.peter.bnp.kata.repository;

import com.peter.bnp.kata.model.Cart;
import com.peter.bnp.kata.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(User user);
}
