package com.example.demo.exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
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
}