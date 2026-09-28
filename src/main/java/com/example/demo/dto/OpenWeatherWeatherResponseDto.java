package com.example.demo.dto;

import lombok.Data;

@Data
public class OpenWeatherWeatherResponseDto {
	private MainData main;
	private WindData wind;
	private WeatherData[] weather;
}