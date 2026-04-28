package com.lifeapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifeapp.auth.AuthContext;
import com.lifeapp.common.FoodPoolType;
import com.lifeapp.common.UnauthorizedException;
import com.lifeapp.dto.CreateFoodRequest;
import com.lifeapp.dto.UpdateFoodRequest;
import com.lifeapp.mapper.FoodDrawRecordMapper;
import com.lifeapp.mapper.FoodMapper;
import com.lifeapp.model.Food;
import com.lifeapp.model.FoodDrawRecord;
import com.lifeapp.service.FoodService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl implements FoodService {

    private final FoodMapper foodMapper;
    private final FoodDrawRecordMapper foodDrawRecordMapper;

    public FoodServiceImpl(FoodMapper foodMapper, FoodDrawRecordMapper foodDrawRecordMapper) {
        this.foodMapper = foodMapper;
        this.foodDrawRecordMapper = foodDrawRecordMapper;
    }

    @Override
    public List<Food> list(String category, String poolView) {
        Long userId = currentUserId();
        String normalizedCategory = normalizeCategory(category);
        return listVisibleFoods(normalizedCategory, userId);
    }

    @Override
    public Food create(CreateFoodRequest request) {
        Food food = new Food();
        LocalDateTime now = LocalDateTime.now();
        food.setUserId(currentUserId());
        food.setPoolType(FoodPoolType.normalize(request.getPoolType(), FoodPoolType.PUBLIC));
        food.setName(request.getName());
        food.setCategory(request.getCategory());
        food.setImageUrl(request.getImageUrl());
        food.setPriceRange(request.getPriceRange());
        food.setTags(request.getTags() == null ? "" : request.getTags().stream().collect(Collectors.joining(",")));
        food.setEnabled(true);
        food.setCreatedAt(now);
        food.setUpdatedAt(now);
        foodMapper.insert(food);
        return food;
    }

    @Override
    public Food update(Long id, UpdateFoodRequest request) {
        Food food = foodMapper.selectById(id);
        Long userId = currentUserId();
        if (!canManageFood(food, userId)) {
            return null;
        }

        food.setPoolType(FoodPoolType.normalize(request.getPoolType(), FoodPoolType.coerceStoredValue(food.getPoolType())));
        food.setName(request.getName());
        food.setCategory(request.getCategory());
        food.setImageUrl(request.getImageUrl());
        food.setPriceRange(request.getPriceRange());
        food.setUpdatedAt(LocalDateTime.now());
        foodMapper.updateById(food);
        return food;
    }

    @Override
    public void delete(Long id) {
        Food food = foodMapper.selectById(id);
        if (!canManageFood(food, currentUserId())) {
            return;
        }

        food.setEnabled(false);
        food.setUpdatedAt(LocalDateTime.now());
        foodMapper.updateById(food);
    }

    @Override
    public Food randomPick(String category, String poolView) {
        Long userId = currentUserId();
        String normalizedCategory = normalizeCategory(category);
        return pickRandom(listVisibleFoods(normalizedCategory, userId), userId);
    }

    @Override
    public List<FoodDrawRecord> history() {
        LambdaQueryWrapper<FoodDrawRecord> query = new LambdaQueryWrapper<FoodDrawRecord>()
                .eq(FoodDrawRecord::getUserId, currentUserId())
                .orderByDesc(FoodDrawRecord::getCreatedAt)
                .last("limit 20");
        return foodDrawRecordMapper.selectList(query);
    }

    private Food pickRandom(List<Food> candidates, Long userId) {
        if (candidates.isEmpty()) {
            return null;
        }

        int index = (int) (Math.random() * candidates.size());
        Food selected = candidates.get(index);

        FoodDrawRecord record = new FoodDrawRecord();
        record.setUserId(userId);
        record.setFoodId(selected.getId());
        record.setFoodName(selected.getName());
        record.setCreatedAt(LocalDateTime.now());
        foodDrawRecordMapper.insert(record);
        return selected;
    }

    private List<Food> listVisibleFoods(String category, Long userId) {
        LambdaQueryWrapper<Food> query = new LambdaQueryWrapper<Food>()
                .eq(Food::isEnabled, true)
                .and(wrapper -> wrapper.eq(Food::getPoolType, FoodPoolType.PUBLIC).or().eq(Food::getUserId, userId))
                .orderByDesc(Food::getCreatedAt);
        if (category != null) {
            query.eq(Food::getCategory, category);
        }
        return foodMapper.selectList(query);
    }

    private boolean canManageFood(Food food, Long userId) {
        return food != null && food.isEnabled() && userId.equals(food.getUserId());
    }

    private String normalizeCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return null;
        }
        return category.trim();
    }

    private Long currentUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        return userId;
    }
}
