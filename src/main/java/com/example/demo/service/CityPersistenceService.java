package com.example.demo.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CityResponseDto;
import com.example.demo.entity.City;
import com.example.demo.repository.CityRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityPersistenceService {
	private final CityRepository cityRepository;
	private final AuditService auditService;

	@Transactional
	public City saveCityAndAudit(CityResponseDto selectedLocation, String countryCode) {
		String cityName = selectedLocation.getName();
		String state = selectedLocation.getState();
		City city = new City();
		city.setCity(cityName);
		city.setState(state);
		city.setCountryCode(countryCode);
		city.setLatitude(selectedLocation.getLatitude());
		city.setLongitude(selectedLocation.getLongitude());
		City savedCity = cityRepository.save(city);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		auditService.record(username, "Added city: " + savedCity.getCity());
		log.info("City added successfully: {}", savedCity.getCity());
		return savedCity;
	}
}