package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.WeatherRequestDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.service.WeatherService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/weather")
public class WeatherController {

	private final WeatherService weatherService;

	public WeatherController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@PostMapping
	public ResponseEntity<WeatherResponseDto> getWeather(@Valid @RequestBody WeatherRequestDto request) {
		WeatherResponseDto weatherResponseDto = weatherService.getWeather(request);

		return ResponseEntity.ok(weatherResponseDto);
	}

}
