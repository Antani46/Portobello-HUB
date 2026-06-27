package com.university.portobellohub.entity;

import com.university.portobellohub.entity.enums.ClothingSize;
import com.university.portobellohub.entity.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "clothing_items")
@DiscriminatorValue("CLOTHING")
@PrimaryKeyJoinColumn(name = "item_id")
public class ClothingItem extends Item {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ClothingSize size;

    @Column(nullable = false, length = 100)
    private String material;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    public ClothingSize getSize() {
        return size;
    }

    public void setSize(ClothingSize size) {
        this.size = size;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
