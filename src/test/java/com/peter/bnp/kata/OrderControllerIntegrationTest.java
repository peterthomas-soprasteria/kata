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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    void getOrdersSuccess() throws Exception{
        mockMvc.perform(get("/orders")
                    .header("Authorization", "Bearer " + jwtToken)
                    .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getOrdersUnauthorized() throws Exception{
        mockMvc.perform(get("/orders")
                    .contentType("application/json"))
                .andExpect(status().isUnauthorized());
    }
}
