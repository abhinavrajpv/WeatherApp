package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {

	Optional<City> findByCityAndState(String city, String state);

	boolean existsByCityAndState(String city, String state);
}