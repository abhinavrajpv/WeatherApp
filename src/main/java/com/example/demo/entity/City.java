package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
	    uniqueConstraints = @UniqueConstraint(
	        columnNames = {"city", "state", "country_code"}
	    )
	)
@Data
@NoArgsConstructor
public class City {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String city;
	private String state;
	private String countryCode;
	private Double latitude;
	private Double longitude;
}
