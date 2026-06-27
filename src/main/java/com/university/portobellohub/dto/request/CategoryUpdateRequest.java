package com.university.portobellohub.dto.request;

import jakarta.validation.constraints.Size;

public class CategoryUpdateRequest {

    @Size(max = 100)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 120)
    private String slug;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
