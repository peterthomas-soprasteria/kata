package com.peter.bnp.kata;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerUserSuccess(){
        String username = "peter";
        String password = "password";
        String hashedPassword = "hashedPassword";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);

        User registeredUser = authService.registerUser(username, password);

        assertNotNull(registeredUser.getId());
        assertThat(username).isEqualTo(registeredUser.getUsername());
        assertThat(hashedPassword).isEqualTo(registeredUser.getPassword());
        verify(userRepository).save(registeredUser);
    }

    @Test
    void registerUserAlreadyExists() {
        String username = "existingUser";
        String password = "password";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistsException.class, () -> {
            authService.registerUser(username, password);
        });
    }
}
