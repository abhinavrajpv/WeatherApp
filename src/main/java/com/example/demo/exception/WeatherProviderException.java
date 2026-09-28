package com.example.demo.exception;

public class WeatherProviderException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public WeatherProviderException(String message) {
		super(message);
	}

	public WeatherProviderException(String message, Throwable cause) {
		super(message, cause);
	}
}