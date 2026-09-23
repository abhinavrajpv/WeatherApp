package com.example.demo.service;

import java.util.List;

import com.example.demo.dto.CityResponseDto;
import com.example.demo.dto.WeatherResponseDto;

public interface WeatherProvider {

	List<CityResponseDto> resolveLocation(String city, String countryCode);

	WeatherResponseDto getWeather(Double latitude, Double longitude);
}
