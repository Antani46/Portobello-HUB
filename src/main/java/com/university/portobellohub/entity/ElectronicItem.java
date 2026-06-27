package com.university.portobellohub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "electronic_items")
@DiscriminatorValue("ELECTRONIC")
@PrimaryKeyJoinColumn(name = "item_id")
public class ElectronicItem extends Item {

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "battery_health")
    private Integer batteryHealth;

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
}
