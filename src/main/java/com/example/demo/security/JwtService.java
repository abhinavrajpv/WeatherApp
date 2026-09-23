package com.example.demo.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey secretKey = Keys.hmacShaKeyFor("my-super-secret-key-my-super-secret-key-12345".getBytes());

	private final long accessTokenExpiration = 60 * 60 * 1000L;

	private final long refreshTokenExpiration = 7L * 24 * 60 * 60 * 1000;

	public String generateAccessToken(UserDetails userDetails) {

		return Jwts.builder().subject(userDetails.getUsername()).claim("type", "access").issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + accessTokenExpiration)).signWith(secretKey).compact();
	}

	public String generateRefreshToken(UserDetails userDetails) {

		return Jwts.builder().subject(userDetails.getUsername()).claim("type", "refresh").issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration)).signWith(secretKey)
				.compact();
	}

	public String extractUsername(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
	}

	public String extractTokenType(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("type",
				String.class);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {

		String username = extractUsername(token);

		return username.equals(userDetails.getUsername());
	}
}