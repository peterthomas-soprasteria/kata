package com.peter.bnp.kata;

import com.peter.bnp.kata.config.JwtProperties;
import com.peter.bnp.kata.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setup(){
        jwtProperties = new JwtProperties();
        jwtProperties.setSecret("aSuperLongSecretKeyThatShouldBeKeptSecretSecretSecret");
        jwtProperties.setExpiration(3600000);
        jwtTokenUtil = new JwtTokenUtil(jwtProperties);
    }

    // Add test cases here
    @Test
    void generateTokenAndValidate(){
        String username = "peter";
        String token = jwtTokenUtil.generateToken(username);

        assertNotNull(token, "Token should not be null");

        assertTrue(jwtTokenUtil.validateToken(token), "Token should be valid");
        assertEquals(username, jwtTokenUtil.extractUsername(token), "Username should be the same");
    }
}
