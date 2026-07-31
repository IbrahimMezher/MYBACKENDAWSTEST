package com.flutterbackend.eveythingPolicies.policycategories.repository;

import com.flutterbackend.eveythingPolicies.policycategories.domain.PolicyCategories;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolicyCategoriesRepository extends JpaRepository<PolicyCategories, Long> {
    Optional<PolicyCategories> findByCategoryId(Long categoryId);
}
