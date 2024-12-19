package com.peter.bnp.kata.service;

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
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public Cart addItemToCart(String username, Long bookId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setCartItems(new ArrayList<>());
                    return newCart;
                });
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: "+ bookId));

        cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(bookId))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);
                    cartItem.setPrice(cartItem.getQuantity() * book.getPrice());
                }, () -> {
                    CartItem cartItem = new CartItem();
                    cartItem.setBook(book);
                    cartItem.setQuantity(quantity);
                    cartItem.setPrice(quantity * book.getPrice());
                    cartItem.setCart(cart);
                    cart.getCartItems().add(cartItem);
                });

        return cartRepository.save(cart);
    }
}
