package com.university.portobellohub.repository;

import com.university.portobellohub.entity.Item;
import com.university.portobellohub.entity.enums.ItemCondition;
import com.university.portobellohub.entity.enums.ItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findByStatus(ItemStatus status, Pageable pageable);

    Page<Item> findByStatusAndSellerId(ItemStatus status, Long sellerId, Pageable pageable);

    Page<Item> findBySellerId(Long sellerId, Pageable pageable);

    @Query("""
            SELECT i FROM Item i
            WHERE i.status = :status
              AND (:name IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:categoryId IS NULL OR i.category.id = :categoryId)
              AND (:condition IS NULL OR i.condition = :condition)
              AND (:minPrice IS NULL OR i.price >= :minPrice)
              AND (:maxPrice IS NULL OR i.price <= :maxPrice)
            """)
    Page<Item> searchPublishedItems(
            @Param("status") ItemStatus status,
            @Param("name") String name,
            @Param("categoryId") Long categoryId,
            @Param("condition") ItemCondition condition,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query(value = """
            SELECT i.item_type, COUNT(*) AS total
            FROM items i
            WHERE i.status = 'PUBLISHED'
            GROUP BY i.item_type
            """, nativeQuery = true)
    List<Object[]> countPublishedByType();

    @Query(value = """
            SELECT i.id, i.name, i.price, COUNT(oi.id) AS sales_count
            FROM items i
            LEFT JOIN order_items oi ON oi.item_id = i.id
            GROUP BY i.id, i.name, i.price
            ORDER BY sales_count DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findTopSellingItems(@Param("limit") int limit);

    long countByStatus(ItemStatus status);
}
