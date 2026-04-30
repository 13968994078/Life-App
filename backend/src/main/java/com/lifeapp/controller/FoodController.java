package com.lifeapp.controller;

import com.lifeapp.common.ApiResponse;
import com.lifeapp.dto.CreateFoodRequest;
import com.lifeapp.dto.RandomFoodRequest;
import com.lifeapp.dto.UpdateFoodRequest;
import com.lifeapp.model.Food;
import com.lifeapp.model.FoodDrawRecord;
import com.lifeapp.service.FoodService;
import com.lifeapp.vo.FoodListItem;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/list")
    public ApiResponse<List<FoodListItem>> list(@RequestParam(required = false) String category,
                                                @RequestParam(required = false) String poolView) {
        return ApiResponse.ok(foodService.list(category, poolView));
    }

    @PostMapping
    public ApiResponse<Food> create(@Valid @RequestBody CreateFoodRequest request) {
        return ApiResponse.ok(foodService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<Food> update(@PathVariable Long id, @Valid @RequestBody UpdateFoodRequest request) {
        Food result = foodService.update(id, request);
        if (result == null) {
            return ApiResponse.fail("美食不存在、已删除或无权操作");
        }
        return ApiResponse.ok(result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        foodService.delete(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/random")
    public ApiResponse<Food> random(@RequestBody(required = false) RandomFoodRequest request) {
        String category = request == null ? null : request.getCategory();
        String poolView = request == null ? null : request.getPoolView();
        Food result = foodService.randomPick(category, poolView);
        if (result == null) {
            return ApiResponse.fail("当前分类下暂无可抽取的美食");
        }
        return ApiResponse.ok(result);
    }

    @GetMapping("/history")
    public ApiResponse<List<FoodDrawRecord>> history() {
        return ApiResponse.ok(foodService.history());
    }
}
