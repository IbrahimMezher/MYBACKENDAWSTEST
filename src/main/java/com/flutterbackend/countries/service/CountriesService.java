package com.flutterbackend.countries.service;

import com.flutterbackend.countries.domain.Countries;
import com.flutterbackend.countries.dto.CountryRequest;
import com.flutterbackend.countries.repository.CountriesRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class CountriesService {

    private final CountriesRepository countriesRepository;

    public CountriesService(CountriesRepository countriesRepository) {
        this.countriesRepository = countriesRepository;
    }

    public String create(CountryRequest body) {
        Countries country = new Countries();
        country.setCountryName(body.getCountryName());
        country.setCurrency(body.getCurrency());
        country.setRegulations(body.getRegulations());
        country.setThemeColor(body.getThemeColor());
        country.setTaxPercentage(body.getTaxPercentage());
        country.setDeliveryPrice(body.getDeliveryPrice());
        country.setCode(body.getCode());
        countriesRepository.save(country);
        return "Country registered";
    }

    public Countries getById(Long id) {
        return countriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found"));
    }

    public List<Countries> getAll() {
        return countriesRepository.findAll();
    }
}
