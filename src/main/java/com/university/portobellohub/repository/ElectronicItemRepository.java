package com.university.portobellohub.repository;

import com.university.portobellohub.entity.ElectronicItem;
import com.university.portobellohub.entity.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectronicItemRepository extends JpaRepository<ElectronicItem, Long> {

    Page<ElectronicItem> findByBrandAndStatus(String brand, ItemStatus status, Pageable pageable);
}
