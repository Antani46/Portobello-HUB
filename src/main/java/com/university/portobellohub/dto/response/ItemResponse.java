package com.university.portobellohub.dto.response;

import com.university.portobellohub.entity.ClothingItem;
import com.university.portobellohub.entity.ElectronicItem;
import com.university.portobellohub.entity.Item;
import com.university.portobellohub.entity.enums.ItemCondition;
import com.university.portobellohub.entity.enums.ItemStatus;
import com.university.portobellohub.entity.enums.ItemType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemResponse {

    private Long id;
    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private ItemCondition condition;
    private String imageUrl;
    private ItemStatus status;
    private String rejectionReason;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long sellerId;
    private String sellerUsername;
    private Long categoryId;
    private String categoryName;
    private ItemType itemType;
    private String brand;
    private String model;
    private Integer warrantyMonths;
    private Integer batteryHealth;
    private String size;
    private String material;
    private String gender;
    private Double averageRating;
    private Long reviewCount;

    public static ItemResponse fromEntity(Item item) {
        ItemResponse response = new ItemResponse();
        response.setId(item.getId());
        response.setSku(item.getSku());
        response.setName(item.getName());
        response.setDescription(item.getDescription());
        response.setPrice(item.getPrice());
        response.setCondition(item.getCondition());
        response.setImageUrl(item.getImageUrl());
        response.setStatus(item.getStatus());
        response.setRejectionReason(item.getRejectionReason());
        response.setReviewedAt(item.getReviewedAt());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        if (item.getSeller() != null) {
            response.setSellerId(item.getSeller().getId());
            response.setSellerUsername(item.getSeller().getUsername());
        }
        if (item.getCategory() != null) {
            response.setCategoryId(item.getCategory().getId());
            response.setCategoryName(item.getCategory().getName());
        }

        if (item instanceof ElectronicItem electronicItem) {
            response.setItemType(ItemType.ELECTRONIC);
            response.setBrand(electronicItem.getBrand());
            response.setModel(electronicItem.getModel());
            response.setWarrantyMonths(electronicItem.getWarrantyMonths());
            response.setBatteryHealth(electronicItem.getBatteryHealth());
        } else if (item instanceof ClothingItem clothingItem) {
            response.setItemType(ItemType.CLOTHING);
            response.setSize(clothingItem.getSize().name());
            response.setMaterial(clothingItem.getMaterial());
            response.setGender(clothingItem.getGender().name());
        }

        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ItemCondition getCondition() {
        return condition;
    }

    public void setCondition(ItemCondition condition) {
        this.condition = condition;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ItemStatus getStatus() {
        return status;
    }

    public void setStatus(ItemStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Integer getBatteryHealth() {
        return batteryHealth;
    }

    public void setBatteryHealth(Integer batteryHealth) {
        this.batteryHealth = batteryHealth;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }
}
