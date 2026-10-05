package com.example.demo.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.security.JwtAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@WebMvcTest(controllers = SecurityConfigTest.TestController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SecurityConfig securityConfig;

	@MockitoBean
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@BeforeEach
	void setUp() throws Exception {
		doAnswer(invocation -> {
			ServletRequest request = invocation.getArgument(0);
			ServletResponse response = invocation.getArgument(1);
			FilterChain filterChain = invocation.getArgument(2);
			filterChain.doFilter(request, response);
			return null;
		}).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
	}

	@Test
	void authenticationEntryPointShouldReturn401() throws Exception {
		AuthenticationEntryPoint entryPoint = securityConfig.authenticationEntryPoint();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		BadCredentialsException exception = new BadCredentialsException("Invalid credentials");

		entryPoint.commence(request, response, exception);

		assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
		assertEquals("application/json", response.getContentType());
	}

	@Test
	void accessDeniedHandlerShouldReturn403() throws Exception {
		AccessDeniedHandler deniedHandler = securityConfig.accessDeniedHandler();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		AccessDeniedException exception = new AccessDeniedException("Access denied");

		deniedHandler.handle(request, response, exception);

		assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
		assertEquals("application/json", response.getContentType());
	}

	@Test
	void adminCanCreateCity() throws Exception {
		mockMvc.perform(post("/cities").with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
	}

	@Test
	void userCannotCreateCity() throws Exception {
		mockMvc.perform(post("/cities").with(user("user").roles("USER"))).andExpect(status().isForbidden());
	}

	@Test
	void userCanGetCities() throws Exception {
		mockMvc.perform(get("/cities").with(user("user").roles("USER"))).andExpect(status().isNotFound());
	}

	@Test
	void unauthenticatedUserCannotGetCities() throws Exception {
		mockMvc.perform(get("/cities")).andExpect(status().isUnauthorized());
	}

	@Test
	void adminCanDeleteCity() throws Exception {
		mockMvc.perform(delete("/cities/1").with(user("admin").roles("ADMIN"))).andExpect(status().isNotFound());
	}

	@Test
	void userCannotDeleteCity() throws Exception {
		mockMvc.perform(delete("/cities/1").with(user("user").roles("USER"))).andExpect(status().isForbidden());
	}

	@Test
	void authEndpointIsPublic() throws Exception {
		mockMvc.perform(get("/auth/test")).andExpect(status().isNotFound());
	}

	@Test
	void otherEndpointsRequireAuthentication() throws Exception {
		mockMvc.perform(get("/some-protected-endpoint")).andExpect(status().isUnauthorized());
	}

	@Test
	void corsConfigurationShouldBeCorrect() {
		var configuration = securityConfig.corsConfigurationSource().getCorsConfiguration(new MockHttpServletRequest());

		assertEquals(List.of("GET", "POST", "DELETE", "PUT", "PATCH", "OPTIONS"), configuration.getAllowedMethods());

		assertEquals(List.of("Authorization", "Content-Type"), configuration.getAllowedHeaders());
	}

	@Test
	void swaggerAndApiDocsShouldBePublic() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isNotFound());

		mockMvc.perform(get("/swagger-ui.html")).andExpect(status().isNotFound());

		mockMvc.perform(get("/v3/api-docs")).andExpect(status().isNotFound());
	}

	@RestController
	static class TestController {
	}
}