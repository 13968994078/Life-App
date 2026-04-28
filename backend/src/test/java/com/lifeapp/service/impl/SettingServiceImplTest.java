package com.lifeapp.service.impl;

import com.lifeapp.auth.AuthContext;
import com.lifeapp.mapper.UserSettingMapper;
import com.lifeapp.model.UserSetting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SettingServiceImplTest {

    @Test
    void getCreatesDefaultSettingWhenMissing() {
        UserSettingMapper mapper = mock(UserSettingMapper.class);
        when(mapper.selectOne(any())).thenReturn(null);

        SettingServiceImpl service = new SettingServiceImpl(mapper);

        AuthContext.setUserId(1L);
        UserSetting result;
        try {
            result = service.get();
        } finally {
            AuthContext.clear();
        }

        assertNotNull(result);
        assertEquals(Long.valueOf(1L), result.getUserId());
        assertEquals("07:00", result.getWakeTargetTime().toString());
        assertFalse(result.isNotificationEnabled());
        verify(mapper).insert(any(UserSetting.class));
    }
}
