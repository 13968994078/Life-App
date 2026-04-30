package com.lifeapp.vo;

import com.lifeapp.model.Food;

import java.time.LocalDateTime;

public class FoodListItem {

    private Long id;
    private Long userId;
    private String poolType;
    private String name;
    private String category;
    private String imageUrl;
    private String priceRange;
    private String tags;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String creatorName;
    private String creatorAvatar;

    public static FoodListItem from(Food food, String creatorName, String creatorAvatar) {
        FoodListItem item = new FoodListItem();
        item.setId(food.getId());
        item.setUserId(food.getUserId());
        item.setPoolType(food.getPoolType());
        item.setName(food.getName());
        item.setCategory(food.getCategory());
        item.setImageUrl(food.getImageUrl());
        item.setPriceRange(food.getPriceRange());
        item.setTags(food.getTags());
        item.setEnabled(food.isEnabled());
        item.setCreatedAt(food.getCreatedAt());
        item.setUpdatedAt(food.getUpdatedAt());
        item.setCreatorName(creatorName);
        item.setCreatorAvatar(creatorAvatar);
        return item;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPoolType() {
        return poolType;
    }

    public void setPoolType(String poolType) {
        this.poolType = poolType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }
}
