package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.CityRequestDto;
import com.example.demo.entity.City;
import com.example.demo.service.CityService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cities")
public class CityController {

	private final CityService cityService;

	public CityController(CityService cityService) {
		this.cityService = cityService;
	}

	@PostMapping
	public ResponseEntity<City> addCity(@Valid @RequestBody CityRequestDto request) {

		City city = cityService.addCity(request);
		return ResponseEntity.ok(city);
	}

	@GetMapping
	public ResponseEntity<List<City>> getCities() {
		return ResponseEntity.ok(cityService.getCities());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCity(@PathVariable Long id) {

		cityService.deleteCity(id);

		return ResponseEntity.ok("City Deleted Successfully!!");
	}

}
