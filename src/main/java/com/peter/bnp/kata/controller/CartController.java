package com.peter.bnp.kata.controller;

import com.peter.bnp.kata.dto.*;
import com.peter.bnp.kata.model.Cart;
import com.peter.bnp.kata.model.Order;
import com.peter.bnp.kata.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addItemToCart(@RequestParam Long bookId, @RequestParam Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Cart cartUpdated = cartService.addItemToCart(username, bookId, quantity);
        return ResponseEntity.ok(toCartResponse(cartUpdated));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateItemInCart(@RequestParam Long bookId, @RequestParam Integer quantity) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Cart cartUpdated = cartService.updateItemInCart(username, bookId, quantity);
        return ResponseEntity.ok(toCartResponse(cartUpdated));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<CartResponse> removeItemFromCart(@RequestParam Long bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Cart cartUpdated = cartService.removeItemFromCart(username, bookId);
        return ResponseEntity.ok(toCartResponse(cartUpdated));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Cart cart = cartService.getCartForUser(username);
        return ResponseEntity.ok(toCartResponse(cart));
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Order order = cartService.checkout(username);
        return ResponseEntity.ok(toOrderResponse(order));
    }

    private CartResponse toCartResponse(Cart cart){
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(cartItem -> new CartItemResponse(
                        new BookResponse(cartItem.getBook().getId(), cartItem.getBook().getTitle(), cartItem.getBook().getPrice()),
                cartItem.getQuantity()))
                .toList();
        return new CartResponse(items);
    }

    private OrderResponse toOrderResponse(Order order){
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(orderItem -> new OrderItemResponse(
                        new BookResponse(orderItem.getBook().getId(), orderItem.getBook().getTitle(), orderItem.getBook().getPrice()),
                        orderItem.getQuantity(),
                        orderItem.getTotalPrice()))
                .toList();
        return new OrderResponse(items, order.getTotalPrice());
    }
}
