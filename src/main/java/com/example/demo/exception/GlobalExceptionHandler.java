package com.example.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.demo.messages.ResponseMessages;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	@ExceptionHandler(CityNotFoundException.class)
	public ResponseEntity<?> handleCityNotFound(CityNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(CityAlreadyExistsException.class)
	public ResponseEntity<?> handleCityAlreadyExists(CityAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(WeatherProviderException.class)
	public ResponseEntity<?> handleWeatherProvider(WeatherProviderException ex) {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<?> handleUserAlreadyExists(UserAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
		log.error("Unexpected application error", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", ResponseMessages.INTERNAL_SERVER_ERROR));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<?> handleInvalidCredentialsException(InvalidCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<?> handleInvalidRefreshTokenException(InvalidRefreshTokenException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ex.getMessage()));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ResponseMessages.DATA_INTEGRITY_ERROR));
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<String> handleDatabaseError(DataAccessException ex) {
		log.error("Database error occurred", ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseMessages.DATABASE_ERROR);
	}
}