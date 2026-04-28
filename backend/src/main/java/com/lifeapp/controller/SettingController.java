package com.lifeapp.controller;

import com.lifeapp.common.ApiResponse;
import com.lifeapp.dto.UpdateSettingRequest;
import com.lifeapp.model.UserSetting;
import com.lifeapp.service.SettingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/settings")
public class SettingController {

    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping
    public ApiResponse<UserSetting> get() {
        return ApiResponse.ok(settingService.get());
    }

    @PutMapping
    public ApiResponse<UserSetting> update(@Valid @RequestBody UpdateSettingRequest request) {
        return ApiResponse.ok(settingService.update(request));
    }
}
