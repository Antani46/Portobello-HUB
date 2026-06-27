package com.university.portobellohub.service;

import com.university.portobellohub.dto.request.CategoryCreateRequest;
import com.university.portobellohub.dto.request.CategoryUpdateRequest;
import com.university.portobellohub.dto.response.CategoryResponse;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.entity.Category;
import com.university.portobellohub.entity.enums.ItemType;
import com.university.portobellohub.exception.BadRequestException;
import com.university.portobellohub.exception.ResourceNotFoundException;
import com.university.portobellohub.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<CategoryResponse> getCategories(ItemType itemType, Pageable pageable) {
        Page<Category> page = itemType == null
                ? categoryRepository.findAll(pageable)
                : categoryRepository.findByItemType(itemType, pageable);
        return PageResponse.from(page.map(CategoryResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        return CategoryResponse.fromEntity(findCategory(id));
    }

    @Transactional
    public CategoryResponse create(CategoryCreateRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category name already exists");
        }
        categoryRepository.findBySlug(request.getSlug()).ifPresent(category -> {
            throw new BadRequestException("Category slug already exists");
        });

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSlug(request.getSlug());
        category.setItemType(request.getItemType());

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryUpdateRequest request) {
        Category category = findCategory(id);

        if (request.getName() != null && !request.getName().equals(category.getName())) {
            if (categoryRepository.existsByName(request.getName())) {
                throw new BadRequestException("Category name already exists");
            }
            category.setName(request.getName());
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getSlug() != null && !request.getSlug().equals(category.getSlug())) {
            categoryRepository.findBySlug(request.getSlug()).ifPresent(existing -> {
                throw new BadRequestException("Category slug already exists");
            });
            category.setSlug(request.getSlug());
        }

        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Category category = findCategory(id);
        if (!category.getItems().isEmpty()) {
            throw new BadRequestException("Cannot delete category with associated items");
        }
        categoryRepository.delete(category);
    }

    public Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}
