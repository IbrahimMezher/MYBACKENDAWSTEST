package com.flutterbackend.countries.web;

import com.flutterbackend.countries.domain.Countries;
import com.flutterbackend.countries.dto.CountryRequest;
import com.flutterbackend.countries.service.CountriesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/countries")
public class CountriesController {

    private final CountriesService countriesService;

    public CountriesController(CountriesService countriesService) {
        this.countriesService = countriesService;
    }

    @PostMapping("/details")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public String details(@RequestBody CountryRequest body) {
        return countriesService.create(body);
    }

    @GetMapping("/getcountry/{id}")
    public Countries getById(@PathVariable Long id) {
        return countriesService.getById(id);
    }

    @GetMapping("/getAllCountries")
    public List<Countries> getAllCountries() {
        return countriesService.getAll();
    }
}
