package com.peter.bnp.kata;

import com.peter.bnp.kata.model.Book;
import com.peter.bnp.kata.repository.BookRepository;
import com.peter.bnp.kata.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    public void getAllBooksReturnsAllFromRepository() {
        List<Book> mockBooks = List.of(
                new Book(1L, "Java Programming", "James Gosling",10.0),
                new Book(2L, "Python Programming", "Guido van Rossum",20.0)
        );
        when(bookRepository.findAll()).thenReturn(mockBooks);

        List<Book> books = bookService.getAllBooks();
        assert books.size() == 2;
        assertEquals(mockBooks, books);
    }
}
