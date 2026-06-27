package com.university.portobellohub.repository;

import com.university.portobellohub.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByItemId(Long itemId, Pageable pageable);

    boolean existsByUserIdAndItemId(Long userId, Long itemId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.item.id = :itemId")
    Double findAverageRatingByItemId(@Param("itemId") Long itemId);

    long countByItemId(Long itemId);
}
