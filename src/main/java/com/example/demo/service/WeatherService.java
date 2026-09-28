package com.example.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WeatherRequestDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
	private final CityRepository cityRepository;
	private final WeatherCacheService weatherCacheService;
	private final AuditService auditService;

	public WeatherResponseDto getWeather(WeatherRequestDto request) {
		String cityName = request.getCity().trim();
		String stateName = request.getState().trim();
		City city = cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(cityName, stateName)
				.orElseThrow(() -> new CityNotFoundException("City is not configured"));
		WeatherResponseDto response = weatherCacheService.getWeatherFromProvider(city);
		log.info("Weather fetched successfully for city: {}", city.getCity());
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		auditService.record(username, "Viewed weather for: " + city.getCity());
		return response;
	}
}