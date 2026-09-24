package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.entity.AppUser;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private AuditService auditService;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtService jwtService;
	@Mock
	private UserDetailsService userDetailsService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	@Test
	void shouldRegisterUserSuccessfully() {
		RegisterRequestDto request = new RegisterRequestDto();
		request.setUsername("testuser");
		request.setPassword("password");
		request.setRole("user");

		when(userRepository.existsByUsername("testuser")).thenReturn(false);

		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

		authService.register(request);

		verify(userRepository).save(any(AppUser.class));

		verify(passwordEncoder).encode("password");

		verify(auditService).record("testuser", "New User Registered");
	}

	@Test
	void shouldThrowExceptionWhenUserAlreadyExists() {
		RegisterRequestDto request = new RegisterRequestDto();
		request.setUsername("testuser");
		request.setPassword("password");
		request.setRole("user");

		when(userRepository.existsByUsername("testuser")).thenReturn(true);

		assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));

		verify(userRepository, never()).save(any(AppUser.class));
		verify(auditService, never()).record("testuser", "New User Registered");
	}

	@Test
	void shouldLoginSuccessfully() {
		LoginRequestDto request = new LoginRequestDto();

		request.setUsername("testuser");
		request.setPassword("password");
		UserDetails userDetails = User.withUsername("testuser").password("encodedpassword").roles("User").build();

		Authentication authentication = mock(Authentication.class);

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);

		when(authentication.getPrincipal()).thenReturn(userDetails);

		when(jwtService.generateAccessToken(userDetails)).thenReturn("access-token");

		when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");
		AuthResponseDto result = authService.login(request);

		assertEquals("access-token", result.getAccessToken());

		assertEquals("refresh-token", result.getRefreshToken());
	}

	@Test
	void shouldThrowExceptionWhenLoginFails() {

		LoginRequestDto request = new LoginRequestDto();

		request.setUsername("testuser");
		request.setPassword("wrongpassword");

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Invalid credentials"));

		assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
	}
}
