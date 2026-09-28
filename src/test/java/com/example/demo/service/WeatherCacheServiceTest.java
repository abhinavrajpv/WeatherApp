package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.entity.City;

@ExtendWith(MockitoExtension.class)
public class WeatherCacheServiceTest {
	@Mock
	private WeatherProvider weatherProvider;
	@InjectMocks
	private WeatherCacheService weatherCacheService;
	private City city;
	private WeatherResponseDto weatherResponse;

	@BeforeEach
	void setUp() {
		city = new City();
		city.setLatitude(12.97);
		city.setLongitude(77.59);
		city.setCity("Bengaluru");
		city.setState("Karnataka");
		city.setCountryCode("IN");
		weatherResponse = new WeatherResponseDto();
	}

	@Test
	void getWeatherFromProvider_shouldReturnWeatherAndSetCityDetails() {
		when(weatherProvider.getWeather(12.97, 77.59)).thenReturn(weatherResponse);
		WeatherResponseDto result = weatherCacheService.getWeatherFromProvider(city);
		assertSame(weatherResponse, result);
		assertEquals("Bengaluru", result.getCity());
		assertEquals("Karnataka", result.getState());
		assertEquals("IN", result.getCountryCode());
		verify(weatherProvider).getWeather(12.97, 77.59);
	}

	@Test
	void evictWeatherCache_shouldExecuteSuccessfully() {
		weatherCacheService.evictWeatherCache(12.97, 77.59);
	}
}