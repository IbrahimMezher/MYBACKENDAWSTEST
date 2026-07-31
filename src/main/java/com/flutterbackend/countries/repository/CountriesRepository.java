package com.flutterbackend.countries.repository;

import com.flutterbackend.countries.domain.Countries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountriesRepository extends JpaRepository<Countries, Long> {
    Optional<Countries> findByCountryNameIgnoreCase(String countryName);
    Optional<Countries> findByCode(String code);
}
