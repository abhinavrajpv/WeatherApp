package com.example.demo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GlobalExceptionHandlerTest {
	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Test
	void testCityNotFound() {
		CityNotFoundException ex = new CityNotFoundException("City not found");
		ResponseEntity<?> response = handler.handleCityNotFound(ex);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}

	@Test
	void testCityAlreadyExists() {
		CityAlreadyExistsException ex = new CityAlreadyExistsException("City already exists");
		ResponseEntity<?> response = handler.handleCityAlreadyExists(ex);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}

	@Test
	void testWeatherProvider() {
		WeatherProviderException ex = new WeatherProviderException("Weather error");
		ResponseEntity<?> response = handler.handleWeatherProvider(ex);
		assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
	}

	@Test
	void testUserAlreadyExists() {
		UserAlreadyExistsException ex = new UserAlreadyExistsException("User already exists");
		ResponseEntity<?> response = handler.handleUserAlreadyExists(ex);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}

	@Test
	void testRuntimeException() {
		RuntimeException ex = new RuntimeException("Unexpected error");
		ResponseEntity<?> response = handler.handleRuntimeException(ex);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}

	@Test
	void testInvalidCredentials() {
		InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");
		ResponseEntity<?> response = handler.handleInvalidCredentialsException(ex);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	void testInvalidRefreshToken() {
		InvalidRefreshTokenException ex = new InvalidRefreshTokenException("Invalid refresh token");
		ResponseEntity<?> response = handler.handleInvalidRefreshTokenException(ex);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
	}

	@Test
	void testDataIntegrityViolation() {
		DataIntegrityViolationException ex = new DataIntegrityViolationException("Database error");
		ResponseEntity<?> response = handler.handleDataIntegrityViolation(ex);
		assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
	}

	@Test
	void testDatabaseError() {
		DataAccessException ex = mock(DataAccessException.class);
		ResponseEntity<String> response = handler.handleDatabaseError(ex);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
	}
}