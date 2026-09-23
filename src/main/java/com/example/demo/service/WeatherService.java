package com.example.demo.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.WeatherRequestDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

@Service
public class WeatherService {

	private final CityRepository cityRepository;

	private final WeatherProvider weatherProvider;

	private final AuditService auditService;

	public WeatherService(CityRepository cityRepository, WeatherProvider weatherProvider, AuditService auditService) {
		this.cityRepository = cityRepository;
		this.weatherProvider = weatherProvider;
		this.auditService = auditService;
	}

	@Transactional
	public WeatherResponseDto getWeather(WeatherRequestDto request) {

		City city = cityRepository.findByCityIgnoreCaseAndStateIgnoreCase(request.getCity(), request.getState())
				.orElseThrow(() -> new CityNotFoundException("City is not configured"));

		WeatherResponseDto response = getWeatherFromProvider(city);

		auditService.record("SYSTEM", "Viewed weather for: " + city.getCity());

		return response;
	}

	@Cacheable(value = "weather", key = "#city.latitude + ',' + #city.longitude")
	public WeatherResponseDto getWeatherFromProvider(City city) {

		WeatherResponseDto weatherresponseDto = weatherProvider.getWeather(city.getLatitude(), city.getLongitude());
		weatherresponseDto.setCity(city.getCity());
		weatherresponseDto.setState(city.getState());
		weatherresponseDto.setCountryCode(city.getCountryCode());

		return weatherresponseDto;
	}
}