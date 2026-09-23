package com.example.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.WeatherRequestDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WeatherService {

	private final CityRepository cityRepository;

	private final WeatherCacheService weatherCacheService;

	private final AuditService auditService;

	public WeatherService(CityRepository cityRepository, WeatherCacheService weatherCacheService,
			AuditService auditService) {
		this.cityRepository = cityRepository;
		this.weatherCacheService = weatherCacheService;
		this.auditService = auditService;
	}

	@Transactional
	public WeatherResponseDto getWeather(WeatherRequestDto request) {

		log.info("Weather request for city: {}", request.getCity().trim());
		City city = cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(request.getCity(), request.getState())
				.orElseThrow(() -> new CityNotFoundException("City is not configured"));

		WeatherResponseDto response = weatherCacheService.getWeatherFromProvider(city);

		log.info("Weather fetched successfully for city: {}", city.getCity());

		auditService.record("SYSTEM", "Viewed weather for: " + city.getCity());

		return response;
	}
}