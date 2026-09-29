package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;

public class OpenWeatherUriBuilderTest {
	private static final String BASE_URL = "https://api.openweathermap.org";
	private OpenWeatherUriBuilder openWeatherUriBuilder;

	@BeforeEach
	void setUp() {
		openWeatherUriBuilder = new OpenWeatherUriBuilder();
	}

	private UriBuilder newUriBuilder() {
		return new DefaultUriBuilderFactory(BASE_URL).builder();
	}

	@Test
	void buildLocationUri_shouldBuildGeocodingUri() {
		URI uri = openWeatherUriBuilder.buildLocationUri(newUriBuilder(), "Bengaluru", "IN", "test-key");
		assertEquals("api.openweathermap.org", uri.getHost());
		assertEquals("/geo/1.0/direct", uri.getPath());
		assertEquals("q=Bengaluru,IN&limit=5&appid=test-key", uri.getQuery());
	}

	@Test
	void buildWeatherUri_shouldBuildWeatherUriWithMetricUnits() {
		URI uri = openWeatherUriBuilder.buildWeatherUri(newUriBuilder(), 12.9716, 77.5946, "test-key");
		assertEquals("api.openweathermap.org", uri.getHost());
		assertEquals("/data/2.5/weather", uri.getPath());
		assertEquals("lat=12.9716&lon=77.5946&appid=test-key&units=metric", uri.getQuery());
	}
}