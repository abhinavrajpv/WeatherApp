package com.example.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.RefreshRequestDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.entity.AppUser;
import com.example.demo.exception.InvalidCredentialsException;
import com.example.demo.exception.InvalidRefreshTokenException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final AuditService auditService;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void register(RegisterRequestDto request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			log.warn("Registration failed - username already exists: {}", request.getUsername());
			throw new UserAlreadyExistsException("Username already exists");
		}
		AppUser user = new AppUser();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole().toUpperCase());
		userRepository.save(user);
		log.info("User registered successfully: {}", user.getUsername());
		auditService.record(request.getUsername(), "New User Registered");
	}

	public AuthResponseDto login(LoginRequestDto request) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String accessToken = jwtService.generateAccessToken(userDetails);
			String refreshToken = jwtService.generateRefreshToken(userDetails);
			log.info("Login successful for username: {}", userDetails.getUsername());
			String role = userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority()
					.replace("ROLE_", "");
			return new AuthResponseDto(accessToken, refreshToken, role);
		} catch (AuthenticationException ex) {
			log.warn("Login failed for username: {}", request.getUsername());
			throw new InvalidCredentialsException("Invalid username or password");
		}
	}

	public AuthResponseDto refresh(RefreshRequestDto request) {
		try {
			String refreshToken = request.getRefreshToken();
			String type = jwtService.extractTokenType(refreshToken);
			if (!"refresh".equals(type)) {
				log.warn("Invalid token type used for refresh");
				throw new InvalidRefreshTokenException("Invalid refresh token");
			}
			String username = jwtService.extractUsername(refreshToken);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if (!jwtService.isTokenValid(refreshToken, userDetails)) {
				log.warn("Refresh token validation failed for username: {}", username);
				throw new InvalidRefreshTokenException("Invalid refresh token");
			}
			String newAccessToken = jwtService.generateAccessToken(userDetails);
			log.info("Access token refreshed successfully for username: {}", username);
			String role = userDetails.getAuthorities().stream().findFirst().orElseThrow().getAuthority()
					.replace("ROLE_", "");
			return new AuthResponseDto(newAccessToken, refreshToken,role);
		} catch (JwtException ex) {
			log.warn("Invalid refresh token: {}", ex.getMessage());
			throw new InvalidRefreshTokenException("Invalid refresh token");
		}
	}
}
