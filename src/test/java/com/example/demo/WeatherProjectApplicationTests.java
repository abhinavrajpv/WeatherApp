package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "weather.api.key=test-api-key")
class WeatherProjectApplicationTests {

	@Test
	void contextLoads() {
	}

}