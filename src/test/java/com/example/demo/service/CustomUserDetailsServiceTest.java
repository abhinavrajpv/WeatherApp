package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.entity.AppUser;
import com.example.demo.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void loadUserByUsername_shouldReturnUserDetails() {
		AppUser appUser = new AppUser();
		appUser.setUsername("abhinav");
		appUser.setPassword("encodedPassword");
		appUser.setRole("USER");
		when(userRepository.findByUsername("abhinav")).thenReturn(Optional.of(appUser));
		UserDetails result = customUserDetailsService.loadUserByUsername("abhinav");
		assertEquals("abhinav", result.getUsername());
		assertEquals("encodedPassword", result.getPassword());
		assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
		verify(userRepository).findByUsername("abhinav");
	}

	@Test
	void loadUserByUsername_shouldThrowExceptionWhenUserNotFound() {
		when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("unknown"));
		verify(userRepository).findByUsername("unknown");
	}
}