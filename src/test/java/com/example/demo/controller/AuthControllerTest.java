package com.example.demo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.AuthResponseDto;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.RefreshRequestDto;
import com.example.demo.dto.RegisterRequestDto;
import com.example.demo.messages.ResponseMessages;
import com.example.demo.service.AuthService;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
	@Mock
	private AuthService authService;
	@InjectMocks
	private AuthController authController;

	@Test
	void registerTest() {
		RegisterRequestDto request = new RegisterRequestDto();
		request.setUsername("testuser");
		request.setPassword("123456");
		request.setRole("USER");
		doNothing().when(authService).register(request);
		ResponseEntity<ApiResponse> response = authController.register(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(ResponseMessages.USER_REGISTERED, response.getBody().getMessage());
		verify(authService).register(request);
	}

	@Test
	void loginTest() {
		LoginRequestDto request = new LoginRequestDto();
		request.setUsername("testuser");
		request.setPassword("123456");
		AuthResponseDto expected =new AuthResponseDto("access-token", "refresh-token", "USER");
		when(authService.login(request)).thenReturn(expected);
		ResponseEntity<AuthResponseDto> response = authController.login(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(expected, response.getBody());
		verify(authService).login(request);
	}

	@Test
	void refreshTest() {
		RefreshRequestDto request = new RefreshRequestDto();
		request.setRefreshToken("refresh-token");
		AuthResponseDto expected =new AuthResponseDto("new-access-token", "refresh-token", "USER");
		when(authService.refresh(request)).thenReturn(expected);
		ResponseEntity<AuthResponseDto> response = authController.refresh(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(expected, response.getBody());
		verify(authService).refresh(request);
	}
}