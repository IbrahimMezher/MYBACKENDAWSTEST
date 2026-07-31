package com.flutterbackend.eveythingPolicies.policycategories.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "category_fields")
public class CategoryField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "field_name", nullable = false)
    @JsonAlias("fieldKey")
    private String fieldName;

    @Column(name = "field_label", nullable = false)
    private String fieldLabel;

    @Column(name = "field_type", nullable = false)
    private String fieldType = "text";

    @Column(name = "field_options", length = 1000)
    private String fieldOptions;

    @Column(name = "is_required")
    @JsonProperty("isRequired")
    private boolean isRequired = true;

    @Column(name = "display_order")
    private int displayOrder = 0;

    public CategoryField() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    public String getFieldLabel() { return fieldLabel; }
    public void setFieldLabel(String fieldLabel) { this.fieldLabel = fieldLabel; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getFieldOptions() { return fieldOptions; }
    public void setFieldOptions(String fieldOptions) { this.fieldOptions = fieldOptions; }
    @JsonProperty("isRequired")
    public boolean isRequired() { return isRequired; }
    @JsonProperty("isRequired")
    public void setRequired(boolean required) { isRequired = required; }
    public int getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}
