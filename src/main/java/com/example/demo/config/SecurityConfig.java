package com.example.demo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.dto.ApiResponse;
import com.example.demo.messages.ResponseMessages;
import com.example.demo.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final ObjectMapper objectMapper;
	@Value("${cors.allowed-origins:http://localhost:5173}")
	private List<String> allowedOrigins;

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
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint())
						.accessDeniedHandler(accessDeniedHandler()))
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/cities").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/cities/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.GET, "/cities").hasAnyRole("USER", "ADMIN")
						.requestMatchers("/weather/**").hasAnyRole("USER", "ADMIN").anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			objectMapper.writeValue(response.getWriter(), new ApiResponse(ResponseMessages.UNAUTHORIZED));
		};
	}

	@Bean
	AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) -> {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			objectMapper.writeValue(response.getWriter(), new ApiResponse(ResponseMessages.ACCESS_DENIED));
		};
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(allowedOrigins);
		configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}