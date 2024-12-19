package com.peter.bnp.kata;

import com.peter.bnp.kata.exception.BookNotFoundException;
import com.peter.bnp.kata.exception.UserNotFoundException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
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
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0, Cart.class);
            cart.setId(1L);
            return cart;
        });

        Cart updatedCart = cartService.addItemToCart(username, bookId, quantity);

        assertNotNull(updatedCart, "Updated cart should not be null");
        assertEquals(mockUser, updatedCart.getUser(), "Cart should belong to user");
        assertFalse(updatedCart.getCartItems().isEmpty(), "Cart should have items");

        CartItem item = updatedCart.getCartItems().get(0);
        assertEquals(mockBook, item.getBook(), "Item should be for the correct book");
        assertEquals(quantity, item.getQuantity(), "Item should have the correct quantity");

        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addItemToCartCreatesNewItemInExistingCart() {
        String username = "peter";
        Long bookId = 2L;
        int quantity = 3;

        User mockUser = new User();
        mockUser.setUsername(username);

        Book existingBook = new Book(1L,"Java Programming", "James Gosling", 10.0);
        CartItem existingItem = new CartItem();
        existingItem.setBook(existingBook);
        existingItem.setQuantity(2);

        Cart existingCart = new Cart();
        existingCart.setUser(mockUser);
        existingCart.setCartItems(new ArrayList<>(List.of(existingItem)));

        Book newBook = new Book(bookId, "Python Programming", "Guido van Rossum", 20.0);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(existingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(newBook));

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0, Cart.class);
            cart.setId(1L);
            return cart;
        });

        Cart updatedCart = cartService.addItemToCart(username, bookId, quantity);

        assertNotNull(updatedCart, "Updated cart should not be null");
        assertEquals(2, updatedCart.getCartItems().size(), "Cart should have 2 items");
        CartItem addedItem = updatedCart.getCartItems().stream()
                .filter(i -> i.getBook().equals(newBook))
                .findFirst().orElseThrow();
        assertEquals(quantity, addedItem.getQuantity(), "Item should have the correct quantity");
    }

    @Test
    void addItemToCartUpdatesExistingItemInExistingCart() {
        String username = "peter";
        Long bookId = 1L;
        int initialQuantity = 3;
        int addedQuantity = 2;

        User mockUser = new User();
        mockUser.setUsername(username);

        Book existingBook = new Book(1L,"Java Programming", "James Gosling", 10.0);
        CartItem existingItem = new CartItem();
        existingItem.setBook(existingBook);
        existingItem.setQuantity(initialQuantity);

        Cart existingCart = new Cart();
        existingCart.setUser(mockUser);
        existingCart.setCartItems(new ArrayList<>(List.of(existingItem)));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.of(existingCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cart = invocation.getArgument(0, Cart.class);
            cart.setId(1L);
            return cart;
        });

        Cart updatedCart = cartService.addItemToCart(username, bookId, addedQuantity);

        assertNotNull(updatedCart, "Updated cart should not be null");
        assertEquals(1, updatedCart.getCartItems().size(), "Cart should have 1 item");
        CartItem updatedItem = updatedCart.getCartItems().get(0);
        assertEquals(initialQuantity+addedQuantity, updatedItem.getQuantity(), "Item should have the correct quantity");
    }

    @Test
    void addItemToCartThrowsExceptionIfUserNotFound() {
        String username = "peter";
        Long bookId = 1L;
        int quantity = 2;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> cartService.addItemToCart(username, bookId, quantity));
    }

    @Test
    void addItemToCartThrowsExceptionIfBookNotFound() {
        String username = "peter";
        Long bookId = 1L;
        int quantity = 1;

        User mockUser = new User();
        mockUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows((BookNotFoundException.class), () -> cartService.addItemToCart(username, bookId, quantity));
    }

    @Test
    void addItemToCartThrowsExceptionIfQuantityInvalid() {
        String username = "peter";
        Long bookId = 1L;
        int quantity = 0;

        User mockUser = new User();
        mockUser.setUsername(username);

        Book mockBook = new Book(1L,"Java Programming", "James Gosling", 10.0);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(cartRepository.findByUser(mockUser)).thenReturn(Optional.empty());
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(mockBook));

        assertThrows(IllegalArgumentException.class, () -> cartService.addItemToCart(username, bookId, quantity));
    }
}
