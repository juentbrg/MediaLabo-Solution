package com.juent.diabetes_assessment.exception;

import org.junit.jupiter.api.*;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    public void init() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    public void handleRuntimeException_shouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Test runtime error");

        ResponseEntity<String> response = handler.handleRuntimeException(ex);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("An internal error occurred"));
        assertTrue(response.getBody().contains("Test runtime error"));
    }

    @Test
    public void handleGenericException_shouldReturnInternalServerError() {
        Exception ex = new Exception("Generic error");

        ResponseEntity<String> response = handler.handleGenericException(ex);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Unexpected error"));
        assertTrue(response.getBody().contains("Generic error"));
    }

}
