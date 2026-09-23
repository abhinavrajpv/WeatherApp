package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponseDto {

	private String city;
	private String state;
	private String countryCode;
	private Double temperature;
	private Integer humidity;
	private Double windSpeed;
	private String weatherCondition;

}
