package com.example.demo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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

	@Value("${weather.api.key}")
	private String apiKey;

	@Override
	public List<CityResponseDto> resolveLocation(String city, String countryCode) {
		log.info("Calling weather provider for city: {}, {}", city, countryCode);

		try {

			CityResponseDto[] response = restClient.get()
					.uri(uriBuilder -> uriBuilder.path("/geo/1.0/direct").queryParam("q", city + "," + countryCode)
							.queryParam("limit", 5).queryParam("appid", apiKey).build())
					.retrieve().body(CityResponseDto[].class);

			if (response == null || response.length == 0) {

				throw new WeatherProviderException("Location not found");
			}

			return Arrays.asList(response);

		} catch (WeatherProviderException e) {
			log.error("Weather provider failed while finding city: {}", city, e);

			throw e;

		} catch (Exception e) {
			log.error("Weather provider failed while finding city: {}", city, e);

			throw new WeatherProviderException("Failed to retrieve location", e);
		}
	}

	@Override
	public WeatherResponseDto getWeather(Double latitude, Double longitude) {

		log.info("Calling weather API for coordinates: {}, {}", latitude, longitude);

		try {

			OpenWeatherWeatherResponseDto response = restClient.get()
					.uri(uriBuilder -> uriBuilder.path("/data/2.5/weather").queryParam("lat", latitude)
							.queryParam("lon", longitude).queryParam("appid", apiKey).queryParam("units", "metric")
							.build())
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

		} catch (Exception e) {
			log.error("Weather API failed for coordinates: {}, {}", latitude, longitude, e);

			throw new WeatherProviderException("Failed to retrieve weather data", e);
		}
	}
}