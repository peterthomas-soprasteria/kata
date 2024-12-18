package com.peter.bnp.kata;

import com.peter.bnp.kata.model.Book;
import com.peter.bnp.kata.model.Cart;
import com.peter.bnp.kata.model.CartItem;
import com.peter.bnp.kata.model.User;
import com.peter.bnp.kata.repository.BookRepository;
import com.peter.bnp.kata.repository.CartItemRepository;
import com.peter.bnp.kata.repository.CartRepository;
import com.peter.bnp.kata.repository.UserRepository;
import com.peter.bnp.kata.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void addItemToCartCreatesCartAndItemIfNoneExists() {
        String username = "peter";
        Long bookId = 1L;
        int quantity = 2;

        User mockUser = new User();
        mockUser.setUsername(username);

        Book mockBook = new Book(1L,"Java Programming", "James Gosling", 10.0);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(cartItemRepository.findByUser(mockUser)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        Cart updatedCart = cartService.addItemToCart(username, bookId, quantity);

        assertNotNull(updatedCart, "Updated cart should not be null");
        assertEquals(mockUser, updatedCart.getUser(), "Cart should belong to user");
        assertFalse(updatedCart.getCartItems().isEmpty(), "Cart should have items");

        CartItem item = updatedCart.getCartItems().get(0);
        assertEquals(mockBook, item.getBook(), "Item should be for the correct book");
        assertEquals(quantity, item.getQuantity(), "Item should have the correct quantity");

        verify(cartRepository).save(any(Cart.class));
    }

}
