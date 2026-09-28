package com.example.demo.service;

import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;

@Component
public class OpenWeatherUriBuilder {
	public URI buildLocationUri(UriBuilder uriBuilder, String city, String countryCode, String apiKey) {
		return uriBuilder.path("/geo/1.0/direct").queryParam("q", city + "," + countryCode).queryParam("limit", 5)
				.queryParam("appid", apiKey).build();
	}

	public URI buildWeatherUri(UriBuilder uriBuilder, Double latitude, Double longitude, String apiKey) {
		return uriBuilder.path("/data/2.5/weather").queryParam("lat", latitude).queryParam("lon", longitude)
				.queryParam("appid", apiKey).queryParam("units", "metric").build();
	}
}