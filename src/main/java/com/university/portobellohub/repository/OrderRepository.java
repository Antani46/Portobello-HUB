package com.university.portobellohub.repository;

import com.university.portobellohub.entity.Order;
import com.university.portobellohub.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    @Query("""
            SELECT o FROM Order o
            LEFT JOIN FETCH o.orderItems oi
            LEFT JOIN FETCH oi.item
            WHERE o.id = :id
            """)
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = :status")
    BigDecimal sumTotalByStatus(@Param("status") OrderStatus status);

    long countByStatus(OrderStatus status);

    @Query("""
            SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END
            FROM OrderItem oi
            WHERE oi.item.id = :itemId
              AND oi.order.customer.id = :userId
              AND oi.order.status IN ('CONFIRMED', 'SHIPPED', 'DELIVERED')
            """)
    boolean hasUserPurchasedItem(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Query("""
            SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END
            FROM OrderItem oi
            WHERE oi.item.id = :itemId
              AND oi.order.status IN ('PENDING', 'CONFIRMED', 'SHIPPED')
            """)
    boolean existsActiveOrderForItem(@Param("itemId") Long itemId);
}
