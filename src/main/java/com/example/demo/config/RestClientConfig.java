package com.example.demo.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	@Value("${weather.api.base-url}")
	private String baseUrl;

	@Value("${weather.api.connect-timeout}")
	private Duration connectTimeout;

	@Value("${weather.api.read-timeout}")
	private Duration readTimeout;

	@Bean
	public RestClient restClient() {

		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);

		return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
	}
}