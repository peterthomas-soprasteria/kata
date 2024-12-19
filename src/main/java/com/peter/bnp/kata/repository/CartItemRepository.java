package com.peter.bnp.kata.repository;

import com.peter.bnp.kata.model.Book;
import com.peter.bnp.kata.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBook(Book book);
}
