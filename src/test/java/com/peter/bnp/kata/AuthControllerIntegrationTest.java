package com.peter.bnp.kata;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peter.bnp.kata.dto.UserLoginRequest;
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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    void loginUserSuccess() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setPassword("$2a$12$KGEpzopqKLOzwcLTEpEzpuFmTVBldJGjzTC7Q.lpWdhhuKUnY41Mm");
        userRepository.save(existingUser);

        String requestBody = objectMapper.writeValueAsString(new UserLoginRequest("existingUser", "somepassword"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void loginUserInvalidCredentials() throws Exception {
        User existingUser = new User();
        existingUser.setUsername("existingUser");
        existingUser.setPassword("$2a$12$KGEpzopqKLOzwcLTEpEzpuFmTVBldJGjzTC7Q.lpWdhhuKUnY41Mm");
        userRepository.save(existingUser);

        String requestBody = objectMapper.writeValueAsString(new UserLoginRequest("existingUser", "wrongPassword"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginUserNotFound() throws Exception {
        String requestBody = objectMapper.writeValueAsString(new UserLoginRequest("nonExistingUser", "somePassword"));

        mockMvc.perform(post("/auth/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andExpect(status().isUnauthorized());
    }
}
