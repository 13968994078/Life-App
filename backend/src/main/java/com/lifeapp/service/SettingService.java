package com.lifeapp.service;

import com.lifeapp.dto.UpdateSettingRequest;
import com.lifeapp.model.UserSetting;

public interface SettingService {

    UserSetting get();

    UserSetting update(UpdateSettingRequest request);
}
