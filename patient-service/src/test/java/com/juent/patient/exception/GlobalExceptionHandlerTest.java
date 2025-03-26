package com.juent.patient.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlePatientNotFound_shouldReturn404Response() {
        PatientNotFoundException exception = new PatientNotFoundException("42");

        ResponseEntity<Map<String, Object>> response = handler.handlePatientNotFound(exception);

        assertEquals(404, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Patient Not Found", response.getBody().get("error"));
        assertEquals("Patient with ID 42 not found.", response.getBody().get("message"));
        assertTrue(response.getBody().containsKey("timestamp"));
        assertTrue(response.getBody().containsKey("status"));
    }

    @Test
    void handleIllegalArgument_shouldReturn400Response() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid data");

        ResponseEntity<Map<String, Object>> response = handler.handleIllegalArgument(exception);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid data", response.getBody().get("message"));
        assertTrue(response.getBody().containsKey("timestamp"));
        assertTrue(response.getBody().containsKey("status"));
    }

    @Test
    void handleGlobalException_shouldReturn500Response() {
        Exception exception = new Exception("Unexpected failure");

        ResponseEntity<Map<String, Object>> response = handler.handleGlobalException(exception);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("An unexpected error occurred", response.getBody().get("message"));
        assertTrue(response.getBody().containsKey("timestamp"));
        assertTrue(response.getBody().containsKey("status"));
    }
}
