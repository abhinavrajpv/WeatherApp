package com.example.demo.dto;

import lombok.Data;

@Data
public class OpenWeatherWeatherResponseDto {

	private MainData main;

	private WindData wind;

	private WeatherData[] weather;

	@Data
	public static class MainData {

		private Double temp;

		private Integer humidity;
	}

	@Data
	public static class WindData {

		private Double speed;
	}

	@Data
	public static class WeatherData {

		private String main;

		private String description;
	}
}