package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.WeatherRequestDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.service.WeatherService;

@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

	@Mock
	private WeatherService weatherService;

	@InjectMocks
	private WeatherController weatherController;

	@Test
	void getWeather_shouldReturnWeatherResponse() {

		WeatherRequestDto request = new WeatherRequestDto();
		request.setCity("Bengaluru");
		request.setState("Karnataka");

		WeatherResponseDto expected = new WeatherResponseDto("Bengaluru", "Karnataka", "IN", 28.5, 65, 4.2, "Clouds");

		when(weatherService.getWeather(request)).thenReturn(expected);

		ResponseEntity<WeatherResponseDto> response = weatherController.getWeather(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(expected, response.getBody());

		verify(weatherService).getWeather(request);
	}
}