package com.peter.bnp.kata;

import com.peter.bnp.kata.exception.InvalidCredentialsException;
import com.peter.bnp.kata.exception.UserAlreadyExistsException;
import com.peter.bnp.kata.model.User;
import com.peter.bnp.kata.repository.UserRepository;
import com.peter.bnp.kata.service.AuthService;
import com.peter.bnp.kata.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUserSuccess() {
        String username = "peter";
        String password = "password";
        String hashedPassword = "hashedPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0, User.class);
                    user.setId(1L);
                    return user;
                }
        );

        User registeredUser = authService.registerUser(username, password);

        assertNotNull(registeredUser);
        assertThat(username).isEqualTo(registeredUser.getUsername());
        assertThat(hashedPassword).isEqualTo(registeredUser.getPassword());
        verify(userRepository).save(registeredUser);
    }

    @Test
    void registerUserAlreadyExists() {
        String username = "existingUser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerUser(username, password));
    }

    @Test
    void loginUserSuccess(){
        String username = "peter";
        String password = "password";
        String hashedPassword = "hashedPassword";
        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword(hashedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(jwtTokenUtil.generateToken(username)).thenReturn("fake-token");

        String token = authService.loginUser(username, password);

        assertNotNull(token);
        assertEquals("fake-token", token,"Token should be the same");
    }

    @Test
    void loginUserInvalidPassword(){
        String username = "peter";
        String password = "password";
        String hashedPassword = "hashedPassword";
        User existingUser = new User();
        existingUser.setUsername(username);
        existingUser.setPassword(hashedPassword);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(username, password));
    }

    @Test
    void loginUserNotFound(){
        String username = "peter";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.loginUser(username, password));
    }
}
