package com.flutterbackend.eveythingPolicies.policycategories.web;

import com.flutterbackend.eveythingPolicies.policycategories.domain.CategoryField;
import com.flutterbackend.eveythingPolicies.policycategories.domain.PolicyCategories;
import com.flutterbackend.eveythingPolicies.policycategories.repository.CategoryFieldRepository;
import com.flutterbackend.eveythingPolicies.policycategories.service.PolicyCategoriesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/policycategories")
public class PolicyCategoriesController {

    private final PolicyCategoriesService policyCategoriesService;
    private final CategoryFieldRepository categoryFieldRepository;

    public PolicyCategoriesController(PolicyCategoriesService policyCategoriesService,
                                      CategoryFieldRepository categoryFieldRepository) {
        this.policyCategoriesService = policyCategoriesService;
        this.categoryFieldRepository = categoryFieldRepository;
    }

    @GetMapping
    public List<PolicyCategories> getAllCategories() {
        return policyCategoriesService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public ResponseEntity<?> createCategory(@RequestBody Map<String, String> body) {
        String name = body.get("categoryName");
        if (name == null || name.isBlank())
            return ResponseEntity.badRequest().body("categoryName is required");
        PolicyCategories cat = new PolicyCategories();
        cat.setCategoryName(name.trim());
        return ResponseEntity.ok(policyCategoriesService.create(cat));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        policyCategoriesService.delete(id);
        return ResponseEntity.ok("Deleted");
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<?> getFields(@PathVariable Long id) {
        return ResponseEntity.ok(categoryFieldRepository.findByCategoryIdOrderByDisplayOrder(id));
    }

    @PostMapping("/{id}/fields")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public ResponseEntity<?> addField(@PathVariable Long id, @RequestBody CategoryField field) {
        field.setCategoryId(id);
        if (field.getFieldName() == null || field.getFieldName().isBlank())
            return ResponseEntity.badRequest().body("fieldName is required");
        if (field.getFieldLabel() == null || field.getFieldLabel().isBlank())
            return ResponseEntity.badRequest().body("fieldLabel is required");
        field.setFieldName(field.getFieldName().trim().replaceAll("\\s+", "_"));
        field.setFieldLabel(field.getFieldLabel().trim());
        if (categoryFieldRepository.existsByCategoryIdAndFieldNameIgnoreCase(id, field.getFieldName()))
            return ResponseEntity.badRequest().body("Field already exists for this category");
        if (field.getFieldType() == null || field.getFieldType().isBlank())
            field.setFieldType("text");
        field.setFieldType(field.getFieldType().trim().toLowerCase());
        if ("select".equals(field.getFieldType())) {
            if (field.getFieldOptions() == null || field.getFieldOptions().isBlank())
                return ResponseEntity.badRequest().body("fieldOptions is required for select fields");
            field.setFieldOptions(field.getFieldOptions().trim());
        } else {
            field.setFieldOptions(null);
        }
        if (field.getDisplayOrder() <= 0)
            field.setDisplayOrder(categoryFieldRepository.findByCategoryIdOrderByDisplayOrder(id).size() + 1);
        return ResponseEntity.ok(categoryFieldRepository.save(field));
    }

    @DeleteMapping("/{id}/fields/{fieldId}")
    @PreAuthorize("hasAnyRole('admin', 'superadmin')")
    public ResponseEntity<?> deleteField(@PathVariable Long id, @PathVariable Long fieldId) {
        CategoryField field = categoryFieldRepository.findByIdAndCategoryId(fieldId, id)
                .orElseThrow(() -> new RuntimeException("Field not found for this category"));
        categoryFieldRepository.delete(field);
        return ResponseEntity.ok("Field deleted");
    }
}
