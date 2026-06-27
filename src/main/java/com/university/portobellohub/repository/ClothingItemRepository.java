package com.university.portobellohub.repository;

import com.university.portobellohub.entity.ClothingItem;
import com.university.portobellohub.entity.enums.ClothingSize;
import com.university.portobellohub.entity.enums.Gender;
import com.university.portobellohub.entity.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothingItemRepository extends JpaRepository<ClothingItem, Long> {

    Page<ClothingItem> findBySizeAndGenderAndStatus(
            ClothingSize size,
            Gender gender,
            ItemStatus status,
            Pageable pageable
    );
}
