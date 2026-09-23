package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.CityRequestDto;
import com.example.demo.dto.CityResponseDto;
import com.example.demo.entity.City;
import com.example.demo.exception.CityAlreadyExistsException;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.exception.WeatherProviderException;
import com.example.demo.repository.CityRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CityService {
	private final CityRepository cityRepository;
	private final AuditService auditService;
	private final WeatherProvider weatherProvider;

	public CityService(CityRepository cityRepository, AuditService auditService, WeatherProvider weatherProvider) {
		this.cityRepository = cityRepository;
		this.auditService = auditService;
		this.weatherProvider = weatherProvider;
	}

	@Transactional
	public City addCity(CityRequestDto request) {

		String cityName = request.getCity().trim();
		String state = request.getState().trim();
		String countryCode = request.getCountryCode().trim().toUpperCase();

		log.info("Adding city: {}, {}, {}", cityName, state, countryCode);

		if (cityRepository.existsByCityIgnoreCaseAndStateIgnoreCase(request.getCity(), request.getState())) {
			log.warn("City already exists: {}", cityName);
			throw new CityAlreadyExistsException("City ALready Exists!!!");
		}

		List<CityResponseDto> locations = weatherProvider.resolveLocation(request.getCity(), request.getCountryCode());

		CityResponseDto selectedLocation = null;

		for (CityResponseDto location : locations) {

			if (location.getState() != null && location.getState().equalsIgnoreCase(request.getState())) {

				selectedLocation = location;
				break;
			}
		}

		if (selectedLocation == null) {
			  log.warn("State mismatch for city: {}", cityName);

			throw new WeatherProviderException("Could not find city with the given state");
		}

		City city = new City();

		city.setCity(request.getCity());

		city.setState(request.getState());

		city.setCountryCode(request.getCountryCode());

		city.setLatitude(selectedLocation.getLatitude());

		city.setLongitude(selectedLocation.getLongitude());

		City savedCity = cityRepository.save(city);

	    log.info("City added successfully: {}", savedCity.getCity());

		auditService.record("SYSTEM", "Added city: " + savedCity.getCity());

		return savedCity;

	}

	public List<City> getCities() {

		return cityRepository.findAll();
	}

	@Transactional
	public void deleteCity(Long id) {
		 log.info("Deleting city with ID: {}", id);
		City city = cityRepository.findById(id).orElseThrow(() -> new CityNotFoundException("City not found"));
		cityRepository.delete(city);

	    log.info("City deleted successfully: {}", city.getCity());
		auditService.record("SYSTEM", "Deleted city: " + city.getCity());

	}

}
