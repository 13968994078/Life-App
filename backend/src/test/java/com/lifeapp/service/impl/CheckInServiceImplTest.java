package com.lifeapp.service.impl;

import com.lifeapp.auth.AuthContext;
import com.lifeapp.mapper.CheckInRecordMapper;
import com.lifeapp.mapper.UserInfoMapper;
import com.lifeapp.model.CheckInRecord;
import com.lifeapp.model.UserInfo;
import com.lifeapp.service.SettingService;
import com.lifeapp.vo.CheckInBoardItem;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CheckInServiceImplTest {

    @Test
    void publicBoardReturnsAllUsersAndSortsTodayCheckinsFirst() {
        CheckInRecordMapper checkInRecordMapper = mock(CheckInRecordMapper.class);
        UserInfoMapper userInfoMapper = mock(UserInfoMapper.class);
        SettingService settingService = mock(SettingService.class);

        UserInfo alice = user(1L, "alice", "Alice");
        UserInfo bob = user(2L, "bob", "Bob");
        UserInfo carol = user(3L, "carol", "Carol");
        when(userInfoMapper.selectList(any())).thenReturn(Arrays.asList(alice, bob, carol));

        LocalDate today = LocalDate.now();
        when(checkInRecordMapper.selectList(any())).thenReturn(Arrays.asList(
                record(2L, today.minusDays(3), 7, 10, "ON_TIME"),
                record(2L, today.minusDays(2), 7, 5, "ON_TIME"),
                record(2L, today.minusDays(1), 7, 4, "ON_TIME"),
                record(1L, today, 8, 3, "LATE"),
                record(1L, today.minusDays(1), 7, 2, "ON_TIME")
        ));

        CheckInServiceImpl service = new CheckInServiceImpl(checkInRecordMapper, userInfoMapper, settingService);

        AuthContext.setUserId(99L);
        try {
            List<CheckInBoardItem> board = service.publicBoard();

            assertEquals(3, board.size());
            assertEquals("alice", board.get(0).getUsername());
            assertTrue(board.get(0).isTodayChecked());
            assertEquals("LATE", board.get(0).getTodayStatus());
            assertEquals(2, board.get(0).getStreakDays());
            assertEquals(2, board.get(0).getTotalDays());

            assertEquals("bob", board.get(1).getUsername());
            assertFalse(board.get(1).isTodayChecked());
            assertEquals(0, board.get(1).getStreakDays());
            assertEquals(3, board.get(1).getTotalDays());

            assertEquals("carol", board.get(2).getUsername());
            assertFalse(board.get(2).isTodayChecked());
            assertEquals(0, board.get(2).getTotalDays());
            assertNotNull(board.get(0).getTodayCheckTime());
        } finally {
            AuthContext.clear();
        }
    }

    private UserInfo user(Long id, String username, String nickname) {
        UserInfo user = new UserInfo();
        user.setId(id);
        user.setUsername(username);
        user.setNickname(nickname);
        return user;
    }

    private CheckInRecord record(Long userId, LocalDate date, int hour, int minute, String status) {
        CheckInRecord record = new CheckInRecord();
        record.setUserId(userId);
        record.setCheckDate(date);
        record.setCheckTime(LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour, minute));
        record.setStatus(status);
        return record;
    }
}
