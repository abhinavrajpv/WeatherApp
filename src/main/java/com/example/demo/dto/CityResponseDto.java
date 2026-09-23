package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CityResponseDto {


	private String city;
	private String state;
	private String countryCode;
	
	@JsonProperty("lat")
	private Double latitude;
	
	@JsonProperty("lon")
	private Double longitude;
}
