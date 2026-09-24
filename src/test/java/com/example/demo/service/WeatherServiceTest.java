package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.WeatherRequestDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.entity.City;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

	@Mock
	private CityRepository cityRepository;

	@Mock
	private WeatherCacheService weatherCacheService;

	@Mock
	private AuditService auditService;

	@InjectMocks
	private WeatherService weatherService;

	@BeforeEach
	void setUp() {

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testuser", null);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@AfterEach
	void cleanUp() {

		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldReturnWeatherSuccessfully() {

		WeatherRequestDto request = new WeatherRequestDto();

		request.setCity("Bengaluru");
		request.setState("Karnataka");

		City city = new City();

		city.setId(1L);
		city.setCity("Bengaluru");
		city.setState("Karnataka");
		city.setCountryCode("IN");
		city.setLatitude(12.97);
		city.setLongitude(77.59);

		WeatherResponseDto weather = new WeatherResponseDto();

		weather.setCity("Bengaluru");
		weather.setState("Karnataka");
		weather.setCountryCode("IN");
		weather.setTemperature(25.5);
		weather.setHumidity(70);
		weather.setWindSpeed(5.2);
		weather.setWeatherCondition("Cloudy");

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Bengaluru", "Karnataka"))
				.thenReturn(Optional.of(city));

		when(weatherCacheService.getWeatherFromProvider(city)).thenReturn(weather);

		WeatherResponseDto result = weatherService.getWeather(request);

		assertEquals("Bengaluru", result.getCity());

		assertEquals(25.5, result.getTemperature());

		assertEquals(70, result.getHumidity());

		assertEquals(5.2, result.getWindSpeed());

		assertEquals("Cloudy", result.getWeatherCondition());

		verify(weatherCacheService).getWeatherFromProvider(city);

		verify(auditService).record("testuser", "Viewed weather for: Bengaluru");
	}

	@Test
	void shouldThrowExceptionWhenCityNotConfigured() {

		WeatherRequestDto request = new WeatherRequestDto();

		request.setCity("Bengaluru");
		request.setState("Karnataka");

		when(cityRepository.findByCityIgnoreCaseAndStateIgnoreCase("Bengaluru", "Karnataka"))
				.thenReturn(Optional.empty());

		assertThrows(CityNotFoundException.class, () -> weatherService.getWeather(request));

		verify(weatherCacheService, never()).getWeatherFromProvider(any(City.class));

		verify(auditService, never()).record(anyString(), anyString());
	}
}