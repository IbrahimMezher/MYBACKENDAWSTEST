package com.flutterbackend.eveythingPolicies.policycategories.service;

import com.flutterbackend.eveythingPolicies.policycategories.domain.PolicyCategories;
import com.flutterbackend.eveythingPolicies.policycategories.repository.PolicyCategoriesRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class PolicyCategoriesService {

    private final PolicyCategoriesRepository policyCategoriesRepository;

    public PolicyCategoriesService(PolicyCategoriesRepository policyCategoriesRepository) {
        this.policyCategoriesRepository = policyCategoriesRepository;
    }

    public List<PolicyCategories> getAll() {
        return policyCategoriesRepository.findAll();
    }

    public PolicyCategories create(PolicyCategories category) {
        return policyCategoriesRepository.save(category);
    }

    public void delete(Long id) {
        policyCategoriesRepository.deleteById(id);
    }
}
