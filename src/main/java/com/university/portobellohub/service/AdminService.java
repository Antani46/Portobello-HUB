package com.university.portobellohub.service;

import com.university.portobellohub.dto.response.AdminStatsResponse;
import com.university.portobellohub.entity.enums.ItemStatus;
import com.university.portobellohub.entity.enums.OrderStatus;
import com.university.portobellohub.repository.ItemRepository;
import com.university.portobellohub.repository.OrderRepository;
import com.university.portobellohub.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderRepository orderRepository;

    public AdminService(
            UserRepository userRepository,
            ItemRepository itemRepository,
            OrderRepository orderRepository
    ) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        AdminStatsResponse stats = new AdminStatsResponse();
        stats.setTotalUsers(userRepository.count());
        stats.setPublishedItems(itemRepository.countByStatus(ItemStatus.PUBLISHED));
        stats.setPendingItems(itemRepository.countByStatus(ItemStatus.PENDING_REVIEW));
        stats.setTotalOrders(orderRepository.count());
        stats.setDeliveredOrders(orderRepository.countByStatus(OrderStatus.DELIVERED));
        stats.setTotalRevenue(orderRepository.sumTotalByStatus(OrderStatus.DELIVERED));
        stats.setInventoryByType(mapInventoryByType(itemRepository.countPublishedByType()));
        return stats;
    }

    private Map<String, Long> mapInventoryByType(List<Object[]> rows) {
        Map<String, Long> inventory = new HashMap<>();
        for (Object[] row : rows) {
            inventory.put(String.valueOf(row[0]), ((Number) row[1]).longValue());
        }
        return inventory;
    }
}
