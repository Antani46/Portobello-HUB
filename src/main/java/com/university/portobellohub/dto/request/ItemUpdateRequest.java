package com.university.portobellohub.dto.request;

import com.university.portobellohub.entity.enums.ItemCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ItemUpdateRequest {

    @Size(max = 150)
    private String name;

    @Size(max = 5000)
    private String description;

    @DecimalMin("0.01")
    private BigDecimal price;

    private ItemCondition condition;

    private Long categoryId;

    @Size(max = 100)
    private String brand;

    @Size(max = 100)
    private String model;

    private Integer warrantyMonths;

    private Integer batteryHealth;

    private String size;

    @Size(max = 100)
    private String material;

    private String gender;

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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
}
