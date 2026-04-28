package com.lifeapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifeapp.auth.AuthContext;
import com.lifeapp.common.UnauthorizedException;
import com.lifeapp.mapper.CheckInRecordMapper;
import com.lifeapp.mapper.UserInfoMapper;
import com.lifeapp.model.CheckInRecord;
import com.lifeapp.model.UserInfo;
import com.lifeapp.model.UserSetting;
import com.lifeapp.service.CheckInService;
import com.lifeapp.service.SettingService;
import com.lifeapp.vo.CheckInBoardItem;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CheckInServiceImpl implements CheckInService {

    private final CheckInRecordMapper checkInRecordMapper;
    private final UserInfoMapper userInfoMapper;
    private final SettingService settingService;

    public CheckInServiceImpl(CheckInRecordMapper checkInRecordMapper,
                              UserInfoMapper userInfoMapper,
                              SettingService settingService) {
        this.checkInRecordMapper = checkInRecordMapper;
        this.userInfoMapper = userInfoMapper;
        this.settingService = settingService;
    }

    @Override
    public CheckInRecord today() {
        LambdaQueryWrapper<CheckInRecord> query = new LambdaQueryWrapper<CheckInRecord>()
                .eq(CheckInRecord::getUserId, currentUserId())
                .eq(CheckInRecord::getCheckDate, LocalDate.now())
                .last("limit 1");
        return checkInRecordMapper.selectOne(query);
    }

    @Override
    public CheckInRecord checkIn() {
        CheckInRecord existing = today();
        if (existing != null) {
            return existing;
        }

        UserSetting setting = settingService.get();
        LocalDateTime now = LocalDateTime.now();

        CheckInRecord record = new CheckInRecord();
        record.setUserId(currentUserId());
        record.setCheckDate(now.toLocalDate());
        record.setCheckTime(now);
        record.setTargetTime(setting.getWakeTargetTime());
        record.setStatus(now.toLocalTime().isAfter(setting.getWakeTargetTime()) ? "LATE" : "ON_TIME");
        record.setCreatedAt(now);
        checkInRecordMapper.insert(record);
        return record;
    }

    @Override
    public Map<String, Object> statistics() {
        LambdaQueryWrapper<CheckInRecord> query = new LambdaQueryWrapper<CheckInRecord>()
                .eq(CheckInRecord::getUserId, currentUserId())
                .orderByAsc(CheckInRecord::getCheckDate);
        List<CheckInRecord> sorted = checkInRecordMapper.selectList(query).stream()
                .sorted(Comparator.comparing(CheckInRecord::getCheckDate))
                .collect(Collectors.toList());

        int streak = 0;
        LocalDate cursor = LocalDate.now();
        for (int i = sorted.size() - 1; i >= 0; i--) {
            if (sorted.get(i).getCheckDate().equals(cursor)) {
                streak++;
                cursor = cursor.minusDays(1);
            } else if (sorted.get(i).getCheckDate().isBefore(cursor)) {
                break;
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("totalDays", sorted.size());
        data.put("streakDays", streak);
        return data;
    }

    @Override
    public List<CheckInRecord> calendar(int year, int month) {
        LambdaQueryWrapper<CheckInRecord> query = new LambdaQueryWrapper<CheckInRecord>()
                .eq(CheckInRecord::getUserId, currentUserId())
                .orderByDesc(CheckInRecord::getCheckDate);
        return checkInRecordMapper.selectList(query).stream()
                .filter(record -> record.getCheckDate().getYear() == year && record.getCheckDate().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    @Override
    public List<CheckInBoardItem> publicBoard() {
        currentUserId();

        List<UserInfo> users = userInfoMapper.selectList(null);
        List<CheckInRecord> allRecords = checkInRecordMapper.selectList(new LambdaQueryWrapper<CheckInRecord>()
                .orderByDesc(CheckInRecord::getCheckDate)
                .orderByAsc(CheckInRecord::getCheckTime));
        Map<Long, List<CheckInRecord>> recordsByUser = allRecords.stream()
                .collect(Collectors.groupingBy(CheckInRecord::getUserId));
        LocalDate today = LocalDate.now();

        return users.stream()
                .map(user -> toBoardItem(user, recordsByUser.get(user.getId()), today))
                .sorted(Comparator.comparing(CheckInBoardItem::isTodayChecked).reversed()
                        .thenComparing(Comparator.comparingInt(CheckInBoardItem::getStreakDays).reversed())
                        .thenComparing(CheckInBoardItem::getTodayCheckTime, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(CheckInBoardItem::getUserId))
                .collect(Collectors.toList());
    }

    private CheckInBoardItem toBoardItem(UserInfo user, List<CheckInRecord> records, LocalDate today) {
        List<CheckInRecord> userRecords = records == null ? Collections.<CheckInRecord>emptyList() : records;
        CheckInRecord todayRecord = userRecords.stream()
                .filter(record -> today.equals(record.getCheckDate()))
                .findFirst()
                .orElse(null);

        CheckInBoardItem item = new CheckInBoardItem();
        item.setUserId(user.getId());
        item.setUsername(user.getUsername());
        item.setNickname(user.getNickname());
        item.setAvatar(user.getAvatar());
        item.setTodayChecked(todayRecord != null);
        item.setTodayStatus(todayRecord == null ? null : todayRecord.getStatus());
        item.setTodayCheckTime(todayRecord == null ? null : todayRecord.getCheckTime());
        item.setStreakDays(calculateStreak(userRecords, today));
        item.setTotalDays(userRecords.size());
        return item;
    }

    private int calculateStreak(List<CheckInRecord> records, LocalDate today) {
        Set<LocalDate> dates = new HashSet<LocalDate>();
        for (CheckInRecord record : records) {
            dates.add(record.getCheckDate());
        }

        int streak = 0;
        LocalDate cursor = today;
        while (dates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private Long currentUserId() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }
        return userId;
    }
}
