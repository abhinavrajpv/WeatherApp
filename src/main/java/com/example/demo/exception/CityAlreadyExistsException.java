package com.example.demo.exception;

public class CityAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CityAlreadyExistsException(String message) {

		super(message);
	}
}