package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterRequestDto {

	@NotBlank(message = "Username is required")
	private String username;

	@NotBlank(message = "Password is required")
	@Size(min = 6, max = 6, message = "Password must contain exactly 6 characters!!")
	private String password;

	@NotBlank(message = "Role is required")
	@Pattern(regexp = "(?i)USER|ADMIN",message = "Role must be USER or ADMIN")
	private String role;
}
