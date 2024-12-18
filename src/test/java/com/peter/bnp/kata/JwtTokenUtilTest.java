package com.peter.bnp.kata;

import com.peter.bnp.kata.config.JwtProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setup(){
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("a-super-long-secret-key-that-should-be-kept-secret");
        jwtProperties.setExpiration(3600000);
        jwtTokenUtil = new JwtTokenUtil(jwtProperties);
    }

    // Add test cases here
    @Test
    void generateTokenAndValidate(){
        String username = "peter";
        String token = jwtTokenUtil.generateToken(username);

        assertNotNull(token, "Token should not be null");

        assertTrue(jwtTokenUtil.validateToken(token, username), "Token should be valid");
        assertEquals(username, jwtTokenUtil.extractUsername(token), "Username should be the same");
    }
}
