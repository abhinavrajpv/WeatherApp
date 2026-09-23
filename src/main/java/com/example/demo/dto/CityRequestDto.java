package com.example.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityRequestDto {

	private String city;
	private String state;
	private String countryCode;
}
