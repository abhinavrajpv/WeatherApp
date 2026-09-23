package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityRequestDto {

	@NotBlank(message = "City is required!!")
	private String city;

	@NotBlank(message = "State is required!!")
	private String state;

	@NotBlank(message = "Country code is required!!")
	@Size(min = 2, max = 2, message = "Country code must contain exactly 2 characters!!")
	@Pattern(regexp = "^[A-Za-z]{2}$", message = "Country code must contain only letters and exactly 2 letters!!")
	private String countryCode;
}
