package com.so.movietrackerservice.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
class TokenSessionControllerTest {

    @Autowired
    private TokenSessionController controller;

    @Test
    public void contexLoads() throws Exception {
        assertThat(controller).isNotNull();
    }

    @Test
    void testValidToken() {

    }

    @Test
    void testInvalidToken() {

    }

    @Test
    void deleteToken() {

    }

    @Test
    void deleteTokenWithoutAuth() {

    }
}
