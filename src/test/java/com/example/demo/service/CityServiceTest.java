package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.CityRequestDto;
import com.example.demo.dto.CityResponseDto;
import com.example.demo.entity.City;
import com.example.demo.exception.CityAlreadyExistsException;
import com.example.demo.exception.CityNotFoundException;
import com.example.demo.exception.WeatherProviderException;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

	@Mock
	private CityRepository cityRepository;

	@Mock
	private AuditService auditService;

	@Mock
	private WeatherProvider weatherProvider;

	@InjectMocks
	private CityService cityService;

	@BeforeEach
	void setUp() {

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("testuser", null);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@AfterEach
	void cleanUp() {

		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldAddCitySuccessfully() {

		CityRequestDto request = new CityRequestDto();

		request.setCity("Bengaluru");
		request.setState("Karnataka");
		request.setCountryCode("IN");

		CityResponseDto location = new CityResponseDto();

		location.setCity("Bengaluru");
		location.setState("Karnataka");
		location.setCountryCode("IN");
		location.setLatitude(12.97);
		location.setLongitude(77.59);

		City savedCity = new City();

		savedCity.setId(1L);
		savedCity.setCity("Bengaluru");
		savedCity.setState("Karnataka");
		savedCity.setCountryCode("IN");
		savedCity.setLatitude(12.97);
		savedCity.setLongitude(77.59);

		when(cityRepository.existsByCityIgnoreCaseAndStateIgnoreCase("Bengaluru", "Karnataka")).thenReturn(false);

		when(weatherProvider.resolveLocation("Bengaluru", "IN")).thenReturn(List.of(location));

		when(cityRepository.save(any(City.class))).thenReturn(savedCity);

		City result = cityService.addCity(request);

		assertEquals("Bengaluru", result.getCity());

		assertEquals("Karnataka", result.getState());

		assertEquals("IN", result.getCountryCode());

		assertEquals(12.97, result.getLatitude());

		assertEquals(77.59, result.getLongitude());

		verify(cityRepository).save(any(City.class));

		verify(auditService).record("testuser", "Added city: Bengaluru");
	}

	@Test
	void shouldThrowExceptionWhenCityAlreadyExists() {

		CityRequestDto request = new CityRequestDto();

		request.setCity("Bengaluru");
		request.setState("Karnataka");
		request.setCountryCode("IN");

		when(cityRepository.existsByCityIgnoreCaseAndStateIgnoreCase("Bengaluru", "Karnataka")).thenReturn(true);

		assertThrows(CityAlreadyExistsException.class, () -> cityService.addCity(request));

		verify(cityRepository, never()).save(any(City.class));

		verify(weatherProvider, never()).resolveLocation(anyString(), anyString());
	}

	@Test
	void shouldThrowExceptionWhenStateDoesNotMatch() {

		CityRequestDto request = new CityRequestDto();

		request.setCity("Bengaluru");
		request.setState("Kerala");
		request.setCountryCode("IN");

		CityResponseDto location = new CityResponseDto();

		location.setCity("Bengaluru");
		location.setState("Karnataka");
		location.setCountryCode("IN");
		location.setLatitude(12.97);
		location.setLongitude(77.59);

		when(cityRepository.existsByCityIgnoreCaseAndStateIgnoreCase("Bengaluru", "Kerala")).thenReturn(false);

		when(weatherProvider.resolveLocation("Bengaluru", "IN")).thenReturn(List.of(location));

		assertThrows(WeatherProviderException.class, () -> cityService.addCity(request));

		verify(cityRepository, never()).save(any(City.class));
	}

	@Test
	void shouldReturnAllCities() {

		City city1 = new City();

		city1.setId(1L);
		city1.setCity("Bengaluru");

		City city2 = new City();

		city2.setId(2L);
		city2.setCity("Chennai");

		when(cityRepository.findAll()).thenReturn(List.of(city1, city2));

		List<City> result = cityService.getCities();

		assertEquals(2, result.size());

		assertEquals("Bengaluru", result.get(0).getCity());

		assertEquals("Chennai", result.get(1).getCity());
	}

	@Test
	void shouldDeleteCitySuccessfully() {

		City city = new City();

		city.setId(1L);
		city.setCity("Bengaluru");

		when(cityRepository.findById(1L)).thenReturn(java.util.Optional.of(city));

		cityService.deleteCity(1L);

		verify(cityRepository).delete(city);

		verify(auditService).record("testuser", "Deleted city: Bengaluru");
	}

	@Test
	void shouldThrowExceptionWhenDeletingNonExistingCity() {

		when(cityRepository.findById(99L)).thenReturn(java.util.Optional.empty());

		assertThrows(CityNotFoundException.class, () -> cityService.deleteCity(99L));

		verify(cityRepository, never()).delete(any(City.class));
	}
}