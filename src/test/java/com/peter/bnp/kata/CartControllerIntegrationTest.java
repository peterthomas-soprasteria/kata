package com.peter.bnp.kata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.bnp.kata.dto.UserLoginRequest;
import com.peter.bnp.kata.dto.UserRegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        //Register a login a test user and get the JWT token
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest("peter", "password");
        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().is2xxSuccessful());

        UserLoginRequest userLoginRequest = new UserLoginRequest("peter", "password");
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        jwtToken = objectMapper.readTree(loginResponse).get("token").asText();
    }

    private void addItemToCart(Long bookId, int quantity) throws Exception {
        mockMvc.perform(post("/cart/add")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType("application/json")
                        .param("bookId", "1")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems").isArray())
                .andExpect(jsonPath("$.cartItems[0].book.id").value(bookId))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(quantity));
    }

    @Test
    void addItemToCartSuccess() throws Exception {
        long bookId = 1L;
        int quantity = 2;

        addItemToCart(bookId, quantity);
    }

    @Test
    void updateItemInCartSuccess() throws Exception{
        long bookId = 1L;
        int quantity = 2;
        int newQuantity = 5;

        addItemToCart(bookId, quantity);

        mockMvc.perform(put("/cart/update")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .param("bookId", "1")
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems").isArray())
                .andExpect(jsonPath("$.cartItems[0].book.id").value(bookId))
                .andExpect(jsonPath("$.cartItems[0].quantity").value(newQuantity));
    }

    @Test
    void removeItemFromCartSuccess() throws Exception{
        long bookId = 1L;
        int quantity = 2;

        addItemToCart(bookId, quantity);

        mockMvc.perform(delete("/cart/remove")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType("application/json")
                .param("bookId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems").isArray())
                .andExpect(jsonPath("$.cartItems").isEmpty());
    }
}
