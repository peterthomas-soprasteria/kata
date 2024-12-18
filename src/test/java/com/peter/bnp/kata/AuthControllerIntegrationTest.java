package com.peter.bnp.kata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.bnp.kata.dto.UserRegistrationRequest;
import com.peter.bnp.kata.model.User;
import com.peter.bnp.kata.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUserSuccess() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new UserRegistrationRequest("newUser", "newPassword"));

        mockMvc.perform(post("/auth/register")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isCreated());

        Optional<User> user = userRepository.findByUsername("newUser");
        assert user.isPresent();
        User newUser = user.get();
        assert newUser.getUsername().equals("newUser");
        assertNotEquals("newPassword", newUser.getPassword());

    }

    @Test
    void registerUserAlreadyExists() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setPassword("password");
        userRepository.save(existingUser);

        String requestBody = objectMapper.writeValueAsString(new UserRegistrationRequest("existingUser", "password"));

        mockMvc.perform(post("/auth/register")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isConflict());
    }
}
