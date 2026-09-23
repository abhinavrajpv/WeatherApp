package com.example.demo.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

@Service
public class AuthService {
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(AuthenticationManager authenticationManager, JwtService jwtService,
			UserDetailsService userDetailsService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void register(RegisterRequestDto request) {

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UserAlreadyExistsException("Username already exists");
		}

		AppUser user = new AppUser();

		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRole(request.getRole().toUpperCase());

		userRepository.save(user);

	}

	public AuthResponseDto login(LoginRequestDto request) {

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();

			String accessToken = jwtService.generateAccessToken(userDetails);

			String refreshToken = jwtService.generateRefreshToken(userDetails);

			return new AuthResponseDto(accessToken, refreshToken);
		} catch (AuthenticationException ex) {

			throw new InvalidCredentialsException("Invalid username or password");
		}
	}

	public AuthResponseDto refresh(RefreshRequestDto request) {

		String refreshToken = request.getRefreshToken();

		String type = jwtService.extractTokenType(refreshToken);

		if (!"refresh".equals(type)) {
			throw new InvalidRefreshTokenException("Invalid refresh token");
		}

		String username = jwtService.extractUsername(refreshToken);

		UserDetails userDetails = userDetailsService.loadUserByUsername(username);

		if (!jwtService.isTokenValid(refreshToken, userDetails)) {

			throw new InvalidRefreshTokenException("Invalid refresh token");
		}

		String newAccessToken = jwtService.generateAccessToken(userDetails);

		return new AuthResponseDto(newAccessToken, refreshToken);

	}

}
