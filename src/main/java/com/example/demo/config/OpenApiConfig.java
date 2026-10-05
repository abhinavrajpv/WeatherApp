package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {
	@Bean
	OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Weather API").version("1.0").description("Weather application REST API"))
				.components(new Components().addSecuritySchemes("oauth2",
						new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
								.flows(new OAuthFlows().password(new OAuthFlow().tokenUrl("/auth/token")))))
				.addSecurityItem(new SecurityRequirement().addList("oauth2"));
	}
}