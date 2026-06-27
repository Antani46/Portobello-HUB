package com.university.portobellohub.dto.response;

import com.university.portobellohub.entity.Order;
import com.university.portobellohub.entity.OrderItem;
import com.university.portobellohub.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddress;
    private String notes;
    private Long customerId;
    private String customerUsername;
    private List<OrderItemResponse> items;

    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setShippingAddress(order.getShippingAddress());
        response.setNotes(order.getNotes());
        if (order.getCustomer() != null) {
            response.setCustomerId(order.getCustomer().getId());
            response.setCustomerUsername(order.getCustomer().getUsername());
        }
        response.setItems(order.getOrderItems().stream()
                .map(OrderItemResponse::fromEntity)
                .toList());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    public static class OrderItemResponse {
        private Long id;
        private Long itemId;
        private String itemName;
        private String itemSku;
        private BigDecimal priceAtPurchase;
        private Integer quantity;

        public static OrderItemResponse fromEntity(OrderItem orderItem) {
            OrderItemResponse response = new OrderItemResponse();
            response.setId(orderItem.getId());
            response.setPriceAtPurchase(orderItem.getPriceAtPurchase());
            response.setQuantity(orderItem.getQuantity());
            if (orderItem.getItem() != null) {
                response.setItemId(orderItem.getItem().getId());
                response.setItemName(orderItem.getItem().getName());
                response.setItemSku(orderItem.getItem().getSku());
            }
            return response;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getItemId() {
            return itemId;
        }

        public void setItemId(Long itemId) {
            this.itemId = itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getItemSku() {
            return itemSku;
        }

        public void setItemSku(String itemSku) {
            this.itemSku = itemSku;
        }

        public BigDecimal getPriceAtPurchase() {
            return priceAtPurchase;
        }

        public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
            this.priceAtPurchase = priceAtPurchase;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
