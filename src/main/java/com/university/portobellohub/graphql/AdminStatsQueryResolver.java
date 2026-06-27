package com.university.portobellohub.graphql;

import com.university.portobellohub.dto.response.AdminStatsResponse;
import com.university.portobellohub.repository.ItemRepository;
import com.university.portobellohub.service.AdminService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class AdminStatsQueryResolver {

    private final AdminService adminService;
    private final ItemRepository itemRepository;

    public AdminStatsQueryResolver(AdminService adminService, ItemRepository itemRepository) {
        this.adminService = adminService;
        this.itemRepository = itemRepository;
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AdminStatsGraphQL adminStats() {
        AdminStatsResponse stats = adminService.getStats();
        AdminStatsGraphQL graphQL = new AdminStatsGraphQL();
        graphQL.setTotalUsers((int) stats.getTotalUsers());
        graphQL.setPublishedItems((int) stats.getPublishedItems());
        graphQL.setPendingItems((int) stats.getPendingItems());
        graphQL.setTotalOrders((int) stats.getTotalOrders());
        graphQL.setDeliveredOrders((int) stats.getDeliveredOrders());
        graphQL.setTotalRevenue(stats.getTotalRevenue() != null ? stats.getTotalRevenue().doubleValue() : 0.0);
        graphQL.setInventoryByType(stats.getInventoryByType().entrySet().stream()
                .map(entry -> new InventoryCountGraphQL(entry.getKey(), entry.getValue().intValue()))
                .toList());
        return graphQL;
    }

    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<TopSellingItemGraphQL> topSellingItems(@Argument int limit) {
        return itemRepository.findTopSellingItems(limit).stream()
                .map(row -> {
                    TopSellingItemGraphQL item = new TopSellingItemGraphQL();
                    item.setId(String.valueOf(row[0]));
                    item.setName(String.valueOf(row[1]));
                    item.setPrice(((BigDecimal) row[2]).doubleValue());
                    item.setSalesCount(((Number) row[3]).intValue());
                    return item;
                })
                .toList();
    }

    public static class AdminStatsGraphQL {
        private int totalUsers;
        private int publishedItems;
        private int pendingItems;
        private int totalOrders;
        private int deliveredOrders;
        private double totalRevenue;
        private List<InventoryCountGraphQL> inventoryByType;

        public int getTotalUsers() {
            return totalUsers;
        }

        public void setTotalUsers(int totalUsers) {
            this.totalUsers = totalUsers;
        }

        public int getPublishedItems() {
            return publishedItems;
        }

        public void setPublishedItems(int publishedItems) {
            this.publishedItems = publishedItems;
        }

        public int getPendingItems() {
            return pendingItems;
        }

        public void setPendingItems(int pendingItems) {
            this.pendingItems = pendingItems;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public int getDeliveredOrders() {
            return deliveredOrders;
        }

        public void setDeliveredOrders(int deliveredOrders) {
            this.deliveredOrders = deliveredOrders;
        }

        public double getTotalRevenue() {
            return totalRevenue;
        }

        public void setTotalRevenue(double totalRevenue) {
            this.totalRevenue = totalRevenue;
        }

        public List<InventoryCountGraphQL> getInventoryByType() {
            return inventoryByType;
        }

        public void setInventoryByType(List<InventoryCountGraphQL> inventoryByType) {
            this.inventoryByType = inventoryByType;
        }
    }

    public static class InventoryCountGraphQL {
        private String type;
        private int count;

        public InventoryCountGraphQL(String type, int count) {
            this.type = type;
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public static class TopSellingItemGraphQL {
        private String id;
        private String name;
        private double price;
        private int salesCount;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getSalesCount() {
            return salesCount;
        }

        public void setSalesCount(int salesCount) {
            this.salesCount = salesCount;
        }
    }
}
