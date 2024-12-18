package com.peter.bnp.kata.service;

import com.peter.bnp.kata.exception.InvalidCredentialsException;
import com.peter.bnp.kata.exception.UserAlreadyExistsException;
import com.peter.bnp.kata.model.User;
import com.peter.bnp.kata.repository.UserRepository;
import com.peter.bnp.kata.util.JwtTokenUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public User registerUser(String username, String password) throws UserAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException();
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public String loginUser(String username, String password) {
    User existingUser = userRepository.findByUsername(username)
            .orElseThrow(InvalidCredentialsException::new);
    if (passwordEncoder.matches(password, existingUser.getPassword())) {
        return jwtTokenUtil.generateToken(username);
    } else {
        throw new InvalidCredentialsException();
    }
}

}
