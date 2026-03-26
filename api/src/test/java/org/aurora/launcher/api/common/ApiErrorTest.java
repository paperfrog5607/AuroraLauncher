package org.aurora.launcher.api.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    @Test
    void shouldCreateApiError() {
        ApiError error = new ApiError(404, "Not Found", "Resource not found");
        
        assertEquals(404, error.getCode());
        assertEquals("Not Found", error.getMessage());
        assertEquals("Resource not found", error.getDetails());
    }

    @Test
    void shouldCreateSimpleError() {
        ApiError error = new ApiError(500, "Internal Server Error");
        
        assertEquals(500, error.getCode());
        assertEquals("Internal Server Error", error.getMessage());
        assertNull(error.getDetails());
    }

    @Test
    void shouldCheckIsError() {
        ApiError error400 = new ApiError(400, "Bad Request");
        ApiError error404 = new ApiError(404, "Not Found");
        ApiError error500 = new ApiError(500, "Server Error");
        
        assertTrue(error400.isClientError());
        assertTrue(error404.isClientError());
        assertFalse(error404.isServerError());
        assertTrue(error500.isServerError());
    }
}