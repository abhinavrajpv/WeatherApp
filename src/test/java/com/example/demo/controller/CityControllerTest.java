package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.CityRequestDto;
import com.example.demo.entity.City;
import com.example.demo.service.CityService;

@ExtendWith(MockitoExtension.class)
public class CityControllerTest {

	@Mock
	private CityService cityService;

	@InjectMocks
	private CityController cityController;

	@Test
	void addCityTest() {

		CityRequestDto request = new CityRequestDto();
		request.setCity("Bengaluru");
		request.setState("Karnataka");
		request.setCountryCode("IN");

		City city = new City();
		city.setId(1L);
		city.setCity("Bengaluru");
		city.setState("Karnataka");
		city.setCountryCode("IN");

		when(cityService.addCity(request)).thenReturn(city);

		ResponseEntity<City> response = cityController.addCity(request);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(city, response.getBody());

		verify(cityService).addCity(request);
	}

	@Test
	void getCities_ShouldReturnCities() {

		List<City> cities = List.of(new City(), new City());

		when(cityService.getCities()).thenReturn(cities);

		ResponseEntity<List<City>> response = cityController.getCities();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(2, response.getBody().size());

		verify(cityService).getCities();
	}

	@Test
	void getCities_whenNoCities_shouldReturnEmptyList() {

		when(cityService.getCities()).thenReturn(List.of());

		ResponseEntity<List<City>> response = cityController.getCities();

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().isEmpty());
	}

	@Test
	void deleteCity_shouldReturnSuccessMessage() {

		doNothing().when(cityService).deleteCity(1L);

		ResponseEntity<String> response = cityController.deleteCity(1L);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("City Deleted Successfully!!", response.getBody());

		verify(cityService).deleteCity(1L);
	}
}