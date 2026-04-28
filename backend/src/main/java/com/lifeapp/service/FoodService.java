package com.lifeapp.service;

import com.lifeapp.dto.CreateFoodRequest;
import com.lifeapp.dto.UpdateFoodRequest;
import com.lifeapp.model.Food;
import com.lifeapp.model.FoodDrawRecord;

import java.util.List;

public interface FoodService {

    List<Food> list(String category, String poolView);

    Food create(CreateFoodRequest request);

    Food update(Long id, UpdateFoodRequest request);

    void delete(Long id);

    Food randomPick(String category, String poolView);

    List<FoodDrawRecord> history();
}
