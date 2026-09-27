package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

public class JwtServiceTest {

	private JwtService jwtService;
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {

		jwtService = new JwtService();

		ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 60000L);

		ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 604800000L);

		userDetails = User.withUsername("testuser").password("password").roles("USER").build();
	}

	@Test
	void shouldGenerateAccessToken() {

		String token = jwtService.generateAccessToken(userDetails);

		assertNotNull(token);
		assertEquals("testuser", jwtService.extractUsername(token));
		assertEquals("access", jwtService.extractTokenType(token));
	}

	@Test
	void shouldGenerateRefreshToken() {

		String token = jwtService.generateRefreshToken(userDetails);

		assertNotNull(token);
		assertEquals("testuser", jwtService.extractUsername(token));
		assertEquals("refresh", jwtService.extractTokenType(token));
	}

	@Test
	void shouldExtractUsername() {

		String token = jwtService.generateAccessToken(userDetails);

		String username = jwtService.extractUsername(token);

		assertEquals("testuser", username);
	}

	@Test
	void shouldExtractTokenType() {

		String token = jwtService.generateAccessToken(userDetails);

		String tokenType = jwtService.extractTokenType(token);

		assertEquals("access", tokenType);
	}

	@Test
	void shouldReturnTrueWhenTokenUsernameMatches() {

		String token = jwtService.generateAccessToken(userDetails);

		assertTrue(jwtService.isTokenValid(token, userDetails));
	}

	@Test
	void shouldReturnFalseWhenTokenUsernameDoesNotMatch() {

		String token = jwtService.generateAccessToken(userDetails);

		UserDetails anotherUser = User.withUsername("anotheruser").password("password").roles("USER").build();

		assertFalse(jwtService.isTokenValid(token, anotherUser));
	}
}