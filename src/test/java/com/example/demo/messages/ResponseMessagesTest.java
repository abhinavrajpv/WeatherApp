package com.example.demo.messages;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ResponseMessagesTest {
	
	@Test
	void shouldCreateResponseMessagesObject() {
	    new ResponseMessages();
	}

    @Test
    void shouldContainExpectedResponseMessages() {

        assertAll(
            () -> assertEquals(
                "User registered successfully",
                ResponseMessages.USER_REGISTERED
            ),
            () -> assertEquals(
                "City deleted successfully",
                ResponseMessages.CITY_DELETED
            ),
            () -> assertEquals(
                "Unauthorized",
                ResponseMessages.UNAUTHORIZED
            ),
            () -> assertEquals(
                "Access denied",
                ResponseMessages.ACCESS_DENIED
            ),
            () -> assertEquals(
                "A database error occurred. Please try again later.",
                ResponseMessages.DATABASE_ERROR
            ),
            () -> assertEquals(
                "An unexpected error occurred. Please try again later.",
                ResponseMessages.INTERNAL_SERVER_ERROR
            ),
            () -> assertEquals(
                "The request violates a database constraint.",
                ResponseMessages.DATA_INTEGRITY_ERROR
            )
        );
    }
}