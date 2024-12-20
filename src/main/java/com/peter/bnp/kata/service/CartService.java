package com.peter.bnp.kata.service;

import com.peter.bnp.kata.exception.BookNotFoundException;
import com.peter.bnp.kata.exception.UserNotFoundException;
import com.peter.bnp.kata.model.*;
import com.peter.bnp.kata.repository.BookRepository;
import com.peter.bnp.kata.repository.CartRepository;
import com.peter.bnp.kata.repository.OrderRepository;
import com.peter.bnp.kata.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    public CartService(CartRepository cartRepository, UserRepository userRepository, BookRepository bookRepository, OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
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

    public Cart updateItemInCart(String username, Long bookId, Integer quantity) {
        Cart cart = cartRepository.findByUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username)))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + username));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));

        cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getBook().getId().equals(bookId))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                    if (quantity <= 0) {
                        cart.getCartItems().remove(cartItem);
                    } else {
                        cartItem.setQuantity(quantity);
                        cartItem.setPrice(quantity * book.getPrice());//we want the current price of the book
                    }
                }, () -> {
                    throw new BookNotFoundException("Book not found in cart: " + bookId);
                });

        return cartRepository.save(cart);
    }

    public Cart removeItemFromCart(String username, Long bookId) {
        Cart cart = cartRepository.findByUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username)))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + username));

        bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));

        cart.getCartItems().removeIf(item -> item.getBook().getId().equals(bookId));

        return cartRepository.save(cart);
    }

    public Cart getCartForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return cartRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + username));
    }

    public Order checkout(String username) {
        Cart cart = cartRepository.findByUser(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username)))
                .orElseThrow(() -> new IllegalArgumentException("Cart not found for user: " + username));

        double totalPrice = cart.getCartItems().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();

        Order order = new Order();
        order.setUser(cart.getUser());
        order.setTotalPrice(totalPrice);
        order.setOrderItems(new ArrayList<>());
        cart.getCartItems().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getPrice());
            order.addOrderItem(orderItem);
        });

        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }
}
