package com.lifeapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifeapp.auth.AuthContext;
import com.lifeapp.common.UnauthorizedException;
import com.lifeapp.dto.UpdateSettingRequest;
import com.lifeapp.mapper.UserSettingMapper;
import com.lifeapp.model.UserSetting;
import com.lifeapp.service.SettingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class SettingServiceImpl implements SettingService {

    private final UserSettingMapper userSettingMapper;

    public SettingServiceImpl(UserSettingMapper userSettingMapper) {
        this.userSettingMapper = userSettingMapper;
    }

    @Override
    public UserSetting get() {
        LambdaQueryWrapper<UserSetting> query = new LambdaQueryWrapper<UserSetting>()
                .eq(UserSetting::getUserId, currentUserId())
                .last("limit 1");
        UserSetting setting = userSettingMapper.selectOne(query);
        if (setting != null) {
            return setting;
        }

        UserSetting defaultSetting = new UserSetting();
        LocalDateTime now = LocalDateTime.now();
        defaultSetting.setUserId(currentUserId());
        defaultSetting.setWakeTargetTime(LocalTime.of(7, 0));
        defaultSetting.setNotificationEnabled(false);
        defaultSetting.setCreatedAt(now);
        defaultSetting.setUpdatedAt(now);
        userSettingMapper.insert(defaultSetting);
        return defaultSetting;
    }

    @Override
    public UserSetting update(UpdateSettingRequest request) {
        UserSetting setting = get();
        setting.setWakeTargetTime(LocalTime.parse(request.getWakeTargetTime()));
        setting.setNotificationEnabled(request.isNotificationEnabled());
        setting.setUpdatedAt(LocalDateTime.now());
        userSettingMapper.updateById(setting);
        return setting;
    }

    private Long currentUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        return userId;
    }
}
