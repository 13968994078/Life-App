package com.lifeapp.dto;

import javax.validation.constraints.NotBlank;

public class UpdateSettingRequest {

    @NotBlank
    private String wakeTargetTime;

    private boolean notificationEnabled;

    public String getWakeTargetTime() {
        return wakeTargetTime;
    }

    public void setWakeTargetTime(String wakeTargetTime) {
        this.wakeTargetTime = wakeTargetTime;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }
}
