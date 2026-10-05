package com.example.demo.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

public class JwtAuthenticationFilterTest {

	private JwtService jwtService;
	private UserDetailsService userDetailsService;
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private FilterChain filterChain;

	private UserDetails userDetails;

	@BeforeEach
	void setUp() {

		jwtService = mock(JwtService.class);
		userDetailsService = mock(UserDetailsService.class);

		jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = mock(FilterChain.class);

		userDetails = User.withUsername("alice").password("password").roles("USER").build();

		SecurityContextHolder.clearContext();
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldContinueWhenAuthorizationHeaderIsMissing() throws ServletException, IOException {

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		verify(jwtService, never()).extractUsername("token");

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldContinueWhenAuthorizationHeaderDoesNotStartWithBearer() throws ServletException, IOException {

		request.addHeader("Authorization", "Basic abc123");

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		verify(jwtService, never()).extractUsername("token");
	}

	@Test
	void shouldAuthenticateWhenAccessTokenIsValid() throws ServletException, IOException {

		request.addHeader("Authorization", "Bearer token");

		when(jwtService.extractUsername("token")).thenReturn("alice");

		when(jwtService.extractTokenType("token")).thenReturn("access");

		when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);

		when(jwtService.isTokenValid("token", userDetails)).thenReturn(true);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		var authentication = SecurityContextHolder.getContext().getAuthentication();

		assertNotNull(authentication);
		assertEquals("alice", authentication.getName());
		assertEquals(userDetails, authentication.getPrincipal());
		assertEquals(Set.copyOf(userDetails.getAuthorities()), Set.copyOf(authentication.getAuthorities()));

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldContinueWhenTokenTypeIsNotAccess() throws ServletException, IOException {

		request.addHeader("Authorization", "Bearer token");

		when(jwtService.extractUsername("token")).thenReturn("alice");

		when(jwtService.extractTokenType("token")).thenReturn("refresh");

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		verify(filterChain).doFilter(request, response);

		verify(userDetailsService, never()).loadUserByUsername("alice");

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldNotAuthenticateWhenUsernameIsNull() throws ServletException, IOException {

		request.addHeader("Authorization", "Bearer token");

		when(jwtService.extractUsername("token")).thenReturn(null);

		when(jwtService.extractTokenType("token")).thenReturn("access");

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		verify(userDetailsService, never()).loadUserByUsername("alice");

		verify(filterChain).doFilter(request, response);

		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void shouldNotLoadUserWhenAlreadyAuthenticated() throws ServletException, IOException {

		request.addHeader("Authorization", "Bearer token");

		var existingAuthentication = new UsernamePasswordAuthenticationToken("existingUser", null, List.of());

		SecurityContextHolder.getContext().setAuthentication(existingAuthentication);

		when(jwtService.extractUsername("token")).thenReturn("alice");

		when(jwtService.extractTokenType("token")).thenReturn("access");

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		verify(userDetailsService, never()).loadUserByUsername("alice");

		assertEquals(existingAuthentication, SecurityContextHolder.getContext().getAuthentication());

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldNotAuthenticateWhenTokenIsInvalid() throws ServletException, IOException {

		request.addHeader("Authorization", "Bearer token");

		when(jwtService.extractUsername("token")).thenReturn("alice");

		when(jwtService.extractTokenType("token")).thenReturn("access");

		when(userDetailsService.loadUserByUsername("alice")).thenReturn(userDetails);

		when(jwtService.isTokenValid("token", userDetails)).thenReturn(false);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldContinueWhenJwtExceptionOccurs() throws ServletException, IOException {

		request.addHeader("Authorization", "Bearer token");

		when(jwtService.extractUsername("token")).thenThrow(new JwtException("Invalid JWT"));

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertNull(SecurityContextHolder.getContext().getAuthentication());

		verify(filterChain).doFilter(request, response);
	}
}