package com.example.demo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.example.demo.dto.CityResponseDto;
import com.example.demo.dto.OpenWeatherWeatherResponseDto;
import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.exception.WeatherProviderException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenWeatherProvider implements WeatherProvider {
	private final RestClient restClient;
	private final OpenWeatherUriBuilder uriBuilder;
	@Value("${weather.api.key}")
	private String apiKey;

	@Override
	public List<CityResponseDto> resolveLocation(String city, String countryCode) {
		try {
			CityResponseDto[] response = restClient.get()
					.uri(uriBuilder -> this.uriBuilder.buildLocationUri(uriBuilder, city, countryCode, apiKey))
					.retrieve().body(CityResponseDto[].class);
			if (response == null || response.length == 0) {
				throw new WeatherProviderException("Location not found");
			}
			return Arrays.asList(response);
		} catch (WeatherProviderException e) {
			log.error("Weather provider failed while finding city: {}", city, e);
			throw e;
		} catch (RestClientResponseException e) {
			log.error("Weather provider returned HTTP status: {}", e.getStatusCode());
			throw new WeatherProviderException("Weather provider returned an error", e);
		} catch (RestClientException e) {
			log.error("Failed to connect to weather provider", e);
			throw new WeatherProviderException("Weather provider is unavailable", e);
		} catch (Exception e) {
			log.error("Weather provider failed while finding city: {}", city);
			throw new WeatherProviderException("Failed to retrieve location", e);
		}
	}

	@Override
	public WeatherResponseDto getWeather(Double latitude, Double longitude) {
		try {
			OpenWeatherWeatherResponseDto response = restClient.get()
					.uri(uriBuilder -> this.uriBuilder.buildWeatherUri(uriBuilder, latitude, longitude, apiKey))
					.retrieve().body(OpenWeatherWeatherResponseDto.class);
			if (response == null) {
				throw new WeatherProviderException("Weather data not received");
			}
			WeatherResponseDto weather = new WeatherResponseDto();
			weather.setTemperature(response.getMain().getTemp());
			weather.setHumidity(response.getMain().getHumidity());
			weather.setWindSpeed(response.getWind().getSpeed());
			if (response.getWeather() != null && response.getWeather().length > 0) {
				weather.setWeatherCondition(response.getWeather()[0].getMain());
			}
			return weather;
		} catch (WeatherProviderException e) {
			log.error("Weather API failed for coordinates: {}, {}", latitude, longitude, e);
			throw e;
		} catch (RestClientResponseException e) {
			log.error("Weather provider returned HTTP status: {}", e.getStatusCode());
			throw new WeatherProviderException("Weather provider returned an error", e);
		} catch (RestClientException e) {
			log.error("Failed to connect to weather provider", e);
			throw new WeatherProviderException("Weather provider is unavailable", e);
		} catch (Exception e) {
			log.error("Weather API failed for coordinates: {}, {}", latitude, longitude);
			throw new WeatherProviderException("Failed to retrieve weather data", e);
		}
	}
}