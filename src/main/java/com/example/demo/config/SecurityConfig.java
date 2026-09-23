package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {

		this.jwtAuthenticationFilter = jwtAuthenticationFilter;

	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				  .exceptionHandling(exception -> exception
				            .authenticationEntryPoint(authenticationEntryPoint())
				            .accessDeniedHandler(accessDeniedHandler())
				        )
				.authorizeHttpRequests(auth -> auth

						.requestMatchers("/auth/**").permitAll()

						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

						.requestMatchers(HttpMethod.POST, "/cities").hasRole("ADMIN")

						.requestMatchers(HttpMethod.DELETE, "/cities/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.GET, "/cities").hasAnyRole("USER", "ADMIN")

						.requestMatchers("/weather/**").hasAnyRole("USER", "ADMIN")

						.anyRequest().authenticated())

				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {

		return (request, response, authException) -> {

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			response.setContentType("application/json");

			response.getWriter().write("{\"error\":\"Unauthorized\"}");
		};
	}

	@Bean
	public AccessDeniedHandler accessDeniedHandler() {

		return (request, response, accessDeniedException) -> {

			response.setStatus(HttpServletResponse.SC_FORBIDDEN);

			response.setContentType("application/json");

			response.getWriter().write("{\"error\":\"Access denied\"}");
		};
	}
}