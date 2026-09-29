package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.dto.CityResponseDto;
import com.example.demo.entity.City;
import com.example.demo.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
public class CityPersistenceServiceTest {
	@Mock
	private CityRepository cityRepository;
	@Mock
	private AuditService auditService;
	@Mock
	private WeatherCacheService weatherCacheService;
	@InjectMocks
	private CityPersistenceService cityPersistenceService;

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
	void shouldSaveCityAndAuditSuccessfully() {
		CityResponseDto location = new CityResponseDto();
		location.setName("Bengaluru");
		location.setState("Karnataka");
		location.setCountry("IN");
		location.setLatitude(12.97);
		location.setLongitude(77.59);
		City savedCity = new City();
		savedCity.setId(1L);
		savedCity.setCity("Bengaluru");
		savedCity.setState("Karnataka");
		savedCity.setCountryCode("IN");
		savedCity.setLatitude(12.97);
		savedCity.setLongitude(77.59);
		when(cityRepository.save(any(City.class))).thenReturn(savedCity);
		City result = cityPersistenceService.saveCityAndAudit(location, "IN");
		assertEquals("Bengaluru", result.getCity());
		assertEquals("Karnataka", result.getState());
		assertEquals("IN", result.getCountryCode());
		assertEquals(12.97, result.getLatitude());
		assertEquals(77.59, result.getLongitude());
		verify(cityRepository).save(any(City.class));
		verify(auditService).record("testuser", "Added city: Bengaluru");
	}

	@Test
	void shouldPropagateExceptionWhenAuditFails() {
		CityResponseDto location = new CityResponseDto();
		location.setName("Bengaluru");
		location.setState("Karnataka");
		location.setCountry("IN");
		location.setLatitude(12.97);
		location.setLongitude(77.59);
		City savedCity = new City();
		savedCity.setId(1L);
		savedCity.setCity("Bengaluru");
		when(cityRepository.save(any(City.class))).thenReturn(savedCity);
		doThrow(new RuntimeException("Audit save failed")).when(auditService).record("testuser",
				"Added city: Bengaluru");
		assertThrows(RuntimeException.class, () -> cityPersistenceService.saveCityAndAudit(location, "IN"));
		verify(cityRepository).save(any(City.class));
		verify(auditService).record("testuser", "Added city: Bengaluru");
	}
}