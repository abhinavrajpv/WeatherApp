package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.example.demo.dto.CityResponseDto;
import com.example.demo.dto.OpenWeatherWeatherResponseDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.exception.WeatherProviderException;

@ExtendWith(MockitoExtension.class)
public class OpenWeatherProviderTest {

	@Mock
	private RestClient restClient;

	@Mock
	private RestClient.RequestHeadersUriSpec requestSpec;

	@Mock
	private RestClient.ResponseSpec responseSpec;

	private OpenWeatherProvider provider;

	@BeforeEach
	void setUp() {

		provider = new OpenWeatherProvider(restClient);

		ReflectionTestUtils.setField(provider, "apiKey", "test-api-key");

		when(restClient.get()).thenReturn(requestSpec);

		when(requestSpec.uri(any(Function.class))).thenReturn(requestSpec);

		when(requestSpec.retrieve()).thenReturn(responseSpec);
	}

	@Test
	void shouldResolveLocationSuccessfully() {

		CityResponseDto city = new CityResponseDto();

		CityResponseDto[] response = { city };

		when(responseSpec.body(CityResponseDto[].class)).thenReturn(response);

		List<CityResponseDto> result = provider.resolveLocation("Bengaluru", "IN");

		assertEquals(1, result.size());
		assertEquals(city, result.get(0));
	}

	@Test
	void shouldThrowExceptionWhenLocationResponseIsNull() {

		when(responseSpec.body(CityResponseDto[].class)).thenReturn(null);

		assertThrows(WeatherProviderException.class, () -> provider.resolveLocation("Bengaluru", "IN"));
	}

	@Test
	void shouldThrowExceptionWhenLocationNotFound() {

		when(responseSpec.body(CityResponseDto[].class)).thenReturn(new CityResponseDto[0]);

		assertThrows(WeatherProviderException.class, () -> provider.resolveLocation("Unknown", "IN"));
	}

	@Test
	void shouldHandleLocationHttpError() {

		when(responseSpec.body(CityResponseDto[].class))
				.thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

		assertThrows(WeatherProviderException.class, () -> provider.resolveLocation("Bengaluru", "IN"));
	}

	@Test
	void shouldHandleLocationConnectionFailure() {

		when(responseSpec.body(CityResponseDto[].class)).thenThrow(new RestClientException("Connection failed"));

		assertThrows(WeatherProviderException.class, () -> provider.resolveLocation("Bengaluru", "IN"));
	}

	@Test
	void shouldGetWeatherSuccessfully() {

		OpenWeatherWeatherResponseDto response = Mockito.mock(OpenWeatherWeatherResponseDto.class,
				Mockito.RETURNS_DEEP_STUBS);

		when(response.getMain().getTemp()).thenReturn(25.0);
		when(response.getMain().getHumidity()).thenReturn(60);
		when(response.getWind().getSpeed()).thenReturn(3.5);

		when(responseSpec.body(OpenWeatherWeatherResponseDto.class)).thenReturn(response);

		WeatherResponseDto result = provider.getWeather(12.9716, 77.5946);

		assertNotNull(result);

		assertEquals(25.0, result.getTemperature());
		assertEquals(60, result.getHumidity());
		assertEquals(3.5, result.getWindSpeed());
	}

	@Test
	void shouldSetWeatherConditionWhenWeatherExists() {

		OpenWeatherWeatherResponseDto response = new OpenWeatherWeatherResponseDto();

		OpenWeatherWeatherResponseDto.MainData main = new OpenWeatherWeatherResponseDto.MainData();
		main.setTemp(25.0);
		main.setHumidity(60);
		response.setMain(main);

		OpenWeatherWeatherResponseDto.WindData wind = new OpenWeatherWeatherResponseDto.WindData();
		wind.setSpeed(3.5);
		response.setWind(wind);

		OpenWeatherWeatherResponseDto.WeatherData weatherInfo = new OpenWeatherWeatherResponseDto.WeatherData();
		weatherInfo.setMain("Clear");

		response.setWeather(new OpenWeatherWeatherResponseDto.WeatherData[] { weatherInfo });

		when(responseSpec.body(OpenWeatherWeatherResponseDto.class)).thenReturn(response);

		WeatherResponseDto result = provider.getWeather(12.9716, 77.5946);

		assertEquals("Clear", result.getWeatherCondition());
	}

	@Test
	void shouldThrowExceptionWhenWeatherResponseIsNull() {

		when(responseSpec.body(OpenWeatherWeatherResponseDto.class)).thenReturn(null);

		assertThrows(WeatherProviderException.class, () -> provider.getWeather(12.9716, 77.5946));
	}

	@Test
	void shouldHandleWeatherHttpError() {

		when(responseSpec.body(OpenWeatherWeatherResponseDto.class))
				.thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

		assertThrows(WeatherProviderException.class, () -> provider.getWeather(12.9716, 77.5946));
	}

	@Test
	void shouldHandleWeatherConnectionFailure() {

		when(responseSpec.body(OpenWeatherWeatherResponseDto.class))
				.thenThrow(new RestClientException("Connection failed"));

		assertThrows(WeatherProviderException.class, () -> provider.getWeather(12.9716, 77.5946));
	}

	@Test
	void shouldHandleUnexpectedLocationException() {

		when(responseSpec.body(CityResponseDto[].class)).thenThrow(new IllegalStateException("Unexpected error"));

		assertThrows(WeatherProviderException.class, () -> provider.resolveLocation("Bengaluru", "IN"));
	}

	@Test
	void shouldHandleUnexpectedWeatherException() {

		when(responseSpec.body(OpenWeatherWeatherResponseDto.class))
				.thenThrow(new IllegalStateException("Unexpected error"));

		assertThrows(WeatherProviderException.class, () -> provider.getWeather(12.9716, 77.5946));
	}
}