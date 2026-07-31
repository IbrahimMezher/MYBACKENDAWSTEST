package com.flutterbackend.eveythingPolicies.policycategories.repository;

import com.flutterbackend.eveythingPolicies.policycategories.domain.CategoryField;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryFieldRepository extends JpaRepository<CategoryField, Long> {
    List<CategoryField> findByCategoryIdOrderByDisplayOrder(Long categoryId);
    Optional<CategoryField> findByIdAndCategoryId(Long id, Long categoryId);
    boolean existsByCategoryIdAndFieldNameIgnoreCase(Long categoryId, String fieldName);
    void deleteByCategoryId(Long categoryId);
}
