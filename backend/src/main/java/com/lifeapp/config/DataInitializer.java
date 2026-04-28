package com.lifeapp.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifeapp.auth.PasswordHasher;
import com.lifeapp.common.FoodPoolType;
import com.lifeapp.mapper.FoodMapper;
import com.lifeapp.mapper.UserInfoMapper;
import com.lifeapp.mapper.UserSettingMapper;
import com.lifeapp.model.Food;
import com.lifeapp.model.UserInfo;
import com.lifeapp.model.UserSetting;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Long DEFAULT_USER_ID = 1L;

    private final FoodMapper foodMapper;
    private final UserInfoMapper userInfoMapper;
    private final UserSettingMapper userSettingMapper;

    public DataInitializer(FoodMapper foodMapper, UserInfoMapper userInfoMapper, UserSettingMapper userSettingMapper) {
        this.foodMapper = foodMapper;
        this.userInfoMapper = userInfoMapper;
        this.userSettingMapper = userSettingMapper;
    }

    @Override
    public void run(String... args) {
        seedDefaultUser();

        if (foodMapper.selectCount(null) == 0) {
            seedFood("煎饼果子", "早餐", "8-12", "快手");
            seedFood("牛肉面", "午餐", "18-25", "面食");
            seedFood("黄焖鸡米饭", "午餐", "20-30", "米饭");
            seedFood("寿司", "晚餐", "30-50", "日料");
            seedFood("炸鸡", "夜宵", "25-40", "高热量");
        }

        LambdaQueryWrapper<UserSetting> query = new LambdaQueryWrapper<UserSetting>()
                .eq(UserSetting::getUserId, DEFAULT_USER_ID)
                .last("limit 1");
        if (userSettingMapper.selectOne(query) == null) {
            UserSetting setting = new UserSetting();
            setting.setUserId(DEFAULT_USER_ID);
            setting.setWakeTargetTime(LocalTime.of(7, 0));
            setting.setNotificationEnabled(false);
            setting.setCreatedAt(LocalDateTime.now());
            setting.setUpdatedAt(LocalDateTime.now());
            userSettingMapper.insert(setting);
        }
    }

    private void seedDefaultUser() {
        LambdaQueryWrapper<UserInfo> query = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getId, DEFAULT_USER_ID)
                .last("limit 1");
        UserInfo existing = userInfoMapper.selectOne(query);
        if (existing != null) {
            boolean changed = false;
            if (existing.getUsername() == null || existing.getUsername().trim().isEmpty()) {
                existing.setUsername("demo");
                changed = true;
            }
            if (existing.getNickname() == null || existing.getNickname().trim().isEmpty()) {
                existing.setNickname("demo");
                changed = true;
            }
            if (existing.getPassword() == null || existing.getPassword().trim().isEmpty()) {
                existing.setPassword(PasswordHasher.hash("123456"));
                changed = true;
            }
            if (changed) {
                existing.setUpdatedAt(LocalDateTime.now());
                userInfoMapper.updateById(existing);
            }
            return;
        }

        UserInfo user = new UserInfo();
        LocalDateTime now = LocalDateTime.now();
        user.setId(DEFAULT_USER_ID);
        user.setUsername("demo");
        user.setNickname("demo");
        user.setPassword(PasswordHasher.hash("123456"));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userInfoMapper.insert(user);
    }

    private void seedFood(String name, String category, String priceRange, String tags) {
        Food food = new Food();
        LocalDateTime now = LocalDateTime.now();
        food.setUserId(DEFAULT_USER_ID);
        food.setPoolType(FoodPoolType.PUBLIC);
        food.setName(name);
        food.setCategory(category);
        food.setPriceRange(priceRange);
        food.setTags(tags);
        food.setEnabled(true);
        food.setCreatedAt(now);
        food.setUpdatedAt(now);
        foodMapper.insert(food);
    }
}
