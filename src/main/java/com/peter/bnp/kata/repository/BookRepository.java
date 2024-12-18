package com.peter.bnp.kata.repository;

import com.peter.bnp.kata.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
