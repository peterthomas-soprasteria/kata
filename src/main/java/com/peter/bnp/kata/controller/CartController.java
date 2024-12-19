package com.peter.bnp.kata.controller;

import com.peter.bnp.kata.dto.BookResponse;
import com.peter.bnp.kata.dto.CartItemResponse;
import com.peter.bnp.kata.dto.CartResponse;
import com.peter.bnp.kata.model.Cart;
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

    private CartResponse toCartResponse(Cart cart){
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(cartItem -> new CartItemResponse(
                        new BookResponse(cartItem.getBook().getId(), cartItem.getBook().getTitle(), cartItem.getBook().getPrice()),
                cartItem.getQuantity()))
                .toList();
        return new CartResponse(items);
    }
}
