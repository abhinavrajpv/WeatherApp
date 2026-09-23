package com.example.demo.service;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.dto.WeatherResponseDto;
import com.example.demo.entity.City;

@Service
public class WeatherCacheService {

    private final WeatherProvider weatherProvider;

    public WeatherCacheService(WeatherProvider weatherProvider) {
        this.weatherProvider = weatherProvider;
    }

    @Cacheable(
        value = "weather",
        key = "#city.latitude + ',' + #city.longitude"
    )
    public WeatherResponseDto getWeatherFromProvider(City city) {

        WeatherResponseDto response =
                weatherProvider.getWeather(
                        city.getLatitude(),
                        city.getLongitude());

        response.setCity(city.getCity());
        response.setState(city.getState());
        response.setCountryCode(city.getCountryCode());

        return response;
    }
}