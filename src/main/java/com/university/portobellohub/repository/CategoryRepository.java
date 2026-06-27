package com.university.portobellohub.repository;

import com.university.portobellohub.entity.Category;
import com.university.portobellohub.entity.enums.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    Page<Category> findByItemType(ItemType itemType, Pageable pageable);

    long countByItemType(ItemType itemType);
}
