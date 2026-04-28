package com.lifeapp.vo;

import java.time.LocalDateTime;

public class CheckInBoardItem {

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private boolean todayChecked;
    private String todayStatus;
    private LocalDateTime todayCheckTime;
    private int streakDays;
    private int totalDays;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isTodayChecked() {
        return todayChecked;
    }

    public void setTodayChecked(boolean todayChecked) {
        this.todayChecked = todayChecked;
    }

    public String getTodayStatus() {
        return todayStatus;
    }

    public void setTodayStatus(String todayStatus) {
        this.todayStatus = todayStatus;
    }

    public LocalDateTime getTodayCheckTime() {
        return todayCheckTime;
    }

    public void setTodayCheckTime(LocalDateTime todayCheckTime) {
        this.todayCheckTime = todayCheckTime;
    }

    public int getStreakDays() {
        return streakDays;
    }

    public void setStreakDays(int streakDays) {
        this.streakDays = streakDays;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
}
