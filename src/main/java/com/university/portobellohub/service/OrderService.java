package com.university.portobellohub.service;

import com.university.portobellohub.dto.request.OrderCreateRequest;
import com.university.portobellohub.dto.request.OrderStatusUpdateRequest;
import com.university.portobellohub.dto.response.OrderResponse;
import com.university.portobellohub.dto.response.PageResponse;
import com.university.portobellohub.entity.Item;
import com.university.portobellohub.entity.Order;
import com.university.portobellohub.entity.OrderItem;
import com.university.portobellohub.entity.User;
import com.university.portobellohub.entity.enums.ItemStatus;
import com.university.portobellohub.entity.enums.OrderStatus;
import com.university.portobellohub.entity.enums.RoleName;
import com.university.portobellohub.exception.BadRequestException;
import com.university.portobellohub.exception.ForbiddenException;
import com.university.portobellohub.exception.ResourceNotFoundException;
import com.university.portobellohub.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ItemService itemService;
    private final SecurityUtils securityUtils;

    public OrderService(OrderRepository orderRepository, ItemService itemService, SecurityUtils securityUtils) {
        this.orderRepository = orderRepository;
        this.itemService = itemService;
        this.securityUtils = securityUtils;
    }

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        // Prendo l'utente loggato
        User customer = securityUtils.getCurrentUser();
        if (customer.getAddress() == null || customer.getAddress().isBlank()) {
            throw new BadRequestException("Please set your shipping address before placing an order");
        }

        Set<Long> uniqueItemIds = new HashSet<>(request.getItemIds());
        if (uniqueItemIds.isEmpty()) {
            throw new BadRequestException("At least one item is required");
        }

        List<Item> items = new ArrayList<>();
        for (Long itemId : uniqueItemIds) {
            Item item = itemService.findItem(itemId);
            if (item.getStatus() != ItemStatus.PUBLISHED) {
                throw new BadRequestException("Item " + itemId + " is not available for purchase");
            }
            if (orderRepository.existsActiveOrderForItem(itemId)) {
                throw new BadRequestException("Item " + itemId + " is already reserved in another order");
            }
            items.add(item);
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(buildShippingAddress(customer));
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (Item item : items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setPriceAtPurchase(item.getPrice());
            orderItem.setQuantity(1);
            order.getOrderItems().add(orderItem);
            total = total.add(item.getPrice());
        }

        order.setTotalAmount(total);
        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getMyOrders(Pageable pageable) {
        User customer = securityUtils.getCurrentUser();
        Page<OrderResponse> page = orderRepository.findByCustomerId(customer.getId(), pageable)
                .map(OrderResponse::fromEntity);
        return PageResponse.from(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getAllOrders(OrderStatus status, Pageable pageable) {
        Page<Order> page = status == null
                ? orderRepository.findAll(pageable)
                : orderRepository.findByStatus(status, pageable);
        return PageResponse.from(page.map(OrderResponse::fromEntity));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = findOrderWithItems(id);
        assertCanView(order);
        return OrderResponse.fromEntity(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatusUpdateRequest request) {
        Order order = findOrderWithItems(id);
        validateStatusTransition(order.getStatus(), request.getStatus());
        order.setStatus(request.getStatus());

        if (request.getStatus() == OrderStatus.CONFIRMED) {
            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.getItem().setStatus(ItemStatus.SOLD);
            }
        }

        return OrderResponse.fromEntity(orderRepository.save(order));
    }

    @Transactional
    public void cancelOrder(Long id) {
        // Recupero l'ordine con gli item
        Order order = findOrderWithItems(id);
        User currentUser = securityUtils.getCurrentUser();
        
        // Controllo se e' admin tramite ciclo
        boolean isAdmin = false;
        for (com.university.portobellohub.entity.Role role : currentUser.getRoles()) {
            if (role.getName() == RoleName.ROLE_ADMIN) {
                isAdmin = true;
                break;
            }
        }

        if (!order.getCustomer().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new ForbiddenException("You are not allowed to cancel this order");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getItem().getStatus() == ItemStatus.SOLD) {
                orderItem.getItem().setStatus(ItemStatus.PUBLISHED);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    private Order findOrderWithItems(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private void assertCanView(Order order) {
        User currentUser = securityUtils.getCurrentUser();
        boolean isOwner = order.getCustomer().getId().equals(currentUser.getId());
        
        // Controllo permessi di staff
        boolean isStaff = false;
        for (com.university.portobellohub.entity.Role role : currentUser.getRoles()) {
            if (role.getName() == RoleName.ROLE_STAFF || role.getName() == RoleName.ROLE_ADMIN) {
                isStaff = true;
                break;
            }
        }
        
        if (!isOwner && !isStaff) {
            throw new ForbiddenException("You are not allowed to view this order");
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new BadRequestException("Cannot update a " + current + " order");
        }
        if (current == OrderStatus.PENDING && next == OrderStatus.CONFIRMED) {
            return;
        }
        if (current == OrderStatus.CONFIRMED && (next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED)) {
            return;
        }
        if (current == OrderStatus.SHIPPED && next == OrderStatus.DELIVERED) {
            return;
        }
        throw new BadRequestException("Invalid status transition from " + current + " to " + next);
    }

    private String buildShippingAddress(User user) {
        return String.format("%s, %s %s", user.getAddress(), user.getPostalCode(), user.getCity());
    }
}
