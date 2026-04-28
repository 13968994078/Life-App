package com.lifeapp.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtUtilTest {

    private static final String TEST_SECRET = "01234567890123456789012345678901";

    @Test
    void parseUserIdReturnsGeneratedSubject() {
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);

        String token = jwtUtil.generateToken(12L, "demo");

        assertEquals(Long.valueOf(12L), jwtUtil.parseUserId(token));
    }
}
