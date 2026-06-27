package com.university.portobellohub.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public class AdminStatsResponse {

    private long totalUsers;
    private long publishedItems;
    private long pendingItems;
    private long totalOrders;
    private long deliveredOrders;
    private BigDecimal totalRevenue;
    private Map<String, Long> inventoryByType;

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getPublishedItems() {
        return publishedItems;
    }

    public void setPublishedItems(long publishedItems) {
        this.publishedItems = publishedItems;
    }

    public long getPendingItems() {
        return pendingItems;
    }

    public void setPendingItems(long pendingItems) {
        this.pendingItems = pendingItems;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getDeliveredOrders() {
        return deliveredOrders;
    }

    public void setDeliveredOrders(long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Map<String, Long> getInventoryByType() {
        return inventoryByType;
    }

    public void setInventoryByType(Map<String, Long> inventoryByType) {
        this.inventoryByType = inventoryByType;
    }
}
