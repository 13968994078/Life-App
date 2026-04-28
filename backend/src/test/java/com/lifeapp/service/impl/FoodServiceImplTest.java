package com.lifeapp.service.impl;

import com.lifeapp.auth.AuthContext;
import com.lifeapp.dto.CreateFoodRequest;
import com.lifeapp.dto.UpdateFoodRequest;
import com.lifeapp.mapper.FoodDrawRecordMapper;
import com.lifeapp.mapper.FoodMapper;
import com.lifeapp.model.Food;
import com.lifeapp.model.FoodDrawRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FoodServiceImplTest {

    @Test
    void listUsesSingleVisibleFoodsQueryForPublicFirstMode() {
        FoodMapper foodMapper = mock(FoodMapper.class);
        FoodDrawRecordMapper recordMapper = mock(FoodDrawRecordMapper.class);

        Food publicFood = food(10L, 99L, "PUBLIC", "公共炒饭");
        Food privateFood = food(11L, 1L, "PRIVATE", "我的汤面");

        when(foodMapper.selectList(any()))
                .thenReturn(Arrays.asList(publicFood, privateFood));

        FoodServiceImpl service = new FoodServiceImpl(foodMapper, recordMapper);

        AuthContext.setUserId(1L);
        try {
            assertEquals(Arrays.asList(publicFood, privateFood), service.list("午餐", "PUBLIC_FIRST"));
        } finally {
            AuthContext.clear();
        }

        verify(foodMapper).selectList(any());
    }

    @Test
    void createDefaultsFoodToPublicPoolWhenPoolTypeMissing() {
        FoodMapper foodMapper = mock(FoodMapper.class);
        FoodDrawRecordMapper recordMapper = mock(FoodDrawRecordMapper.class);
        FoodServiceImpl service = new FoodServiceImpl(foodMapper, recordMapper);
        CreateFoodRequest request = new CreateFoodRequest();
        request.setName("煎饼果子");
        request.setCategory("早餐");
        request.setPriceRange("8-12");

        ArgumentCaptor<Food> captor = ArgumentCaptor.forClass(Food.class);

        AuthContext.setUserId(1L);
        try {
            service.create(request);
        } finally {
            AuthContext.clear();
        }

        verify(foodMapper).insert(captor.capture());
        assertEquals("PUBLIC", captor.getValue().getPoolType());
    }

    @Test
    void updateChangesExistingFood() {
        FoodMapper foodMapper = mock(FoodMapper.class);
        FoodDrawRecordMapper recordMapper = mock(FoodDrawRecordMapper.class);
        Food food = new Food();
        food.setId(1L);
        food.setUserId(1L);
        food.setPoolType("PRIVATE");
        food.setEnabled(true);
        when(foodMapper.selectById(1L)).thenReturn(food);

        FoodServiceImpl service = new FoodServiceImpl(foodMapper, recordMapper);
        UpdateFoodRequest request = new UpdateFoodRequest();
        request.setName("砂锅饭");
        request.setCategory("晚餐");
        request.setPriceRange("25-35");
        request.setPoolType("PRIVATE");

        AuthContext.setUserId(1L);
        Food result;
        try {
            result = service.update(1L, request);
        } finally {
            AuthContext.clear();
        }

        assertNotNull(result);
        assertEquals("砂锅饭", result.getName());
        assertEquals("晚餐", result.getCategory());
        verify(foodMapper).updateById(food);
    }

    @Test
    void updateRejectsPublicFoodFromAnotherCreator() {
        FoodMapper foodMapper = mock(FoodMapper.class);
        FoodDrawRecordMapper recordMapper = mock(FoodDrawRecordMapper.class);
        Food food = food(3L, 9L, "PUBLIC", "共享寿司");
        when(foodMapper.selectById(3L)).thenReturn(food);

        FoodServiceImpl service = new FoodServiceImpl(foodMapper, recordMapper);
        UpdateFoodRequest request = new UpdateFoodRequest();
        request.setName("共享寿司");
        request.setCategory("晚餐");
        request.setPriceRange("30-50");
        request.setPoolType("PUBLIC");

        AuthContext.setUserId(1L);
        try {
            assertNull(service.update(3L, request));
        } finally {
            AuthContext.clear();
        }

        verify(foodMapper, never()).updateById(any(Food.class));
    }

    @Test
    void deleteSoftDisablesFood() {
        FoodMapper foodMapper = mock(FoodMapper.class);
        FoodDrawRecordMapper recordMapper = mock(FoodDrawRecordMapper.class);
        Food food = new Food();
        food.setId(2L);
        food.setUserId(1L);
        food.setPoolType("PRIVATE");
        food.setEnabled(true);
        when(foodMapper.selectById(2L)).thenReturn(food);

        FoodServiceImpl service = new FoodServiceImpl(foodMapper, recordMapper);

        AuthContext.setUserId(1L);
        try {
            service.delete(2L);
        } finally {
            AuthContext.clear();
        }

        assertFalse(food.isEnabled());
        verify(foodMapper).updateById(food);
    }

    @Test
    void randomPickRecordsChosenVisibleFood() {
        FoodMapper foodMapper = mock(FoodMapper.class);
        FoodDrawRecordMapper recordMapper = mock(FoodDrawRecordMapper.class);
        Food privateFood = food(8L, 1L, "PRIVATE", "私藏小火锅");

        when(foodMapper.selectList(any()))
                .thenReturn(Collections.singletonList(privateFood));

        ArgumentCaptor<FoodDrawRecord> captor = ArgumentCaptor.forClass(FoodDrawRecord.class);

        FoodServiceImpl service = new FoodServiceImpl(foodMapper, recordMapper);

        AuthContext.setUserId(1L);
        Food result;
        try {
            result = service.randomPick("晚餐", "PUBLIC_FIRST");
        } finally {
            AuthContext.clear();
        }

        assertEquals(privateFood, result);
        verify(recordMapper).insert(captor.capture());
        assertEquals(Long.valueOf(1L), captor.getValue().getUserId());
        assertEquals(Long.valueOf(8L), captor.getValue().getFoodId());
        assertEquals("私藏小火锅", captor.getValue().getFoodName());
    }

    private Food food(Long id, Long userId, String poolType, String name) {
        Food food = new Food();
        food.setId(id);
        food.setUserId(userId);
        food.setPoolType(poolType);
        food.setName(name);
        food.setCategory("午餐");
        food.setEnabled(true);
        return food;
    }
}
