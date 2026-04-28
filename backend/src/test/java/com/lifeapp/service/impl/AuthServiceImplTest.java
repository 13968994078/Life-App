package com.lifeapp.service.impl;

import com.lifeapp.auth.JwtUtil;
import com.lifeapp.common.BadRequestException;
import com.lifeapp.dto.ChangePasswordRequest;
import com.lifeapp.dto.LoginRequest;
import com.lifeapp.dto.RegisterRequest;
import com.lifeapp.mapper.UserInfoMapper;
import com.lifeapp.model.UserInfo;
import com.lifeapp.vo.LoginResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    private static final String TEST_SECRET = "01234567890123456789012345678901";

    @Test
    void loginMigratesPlaintextPasswordAndSupportsSubsequentLogins() {
        UserInfoMapper mapper = mock(UserInfoMapper.class);
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);
        UserInfo user = new UserInfo();
        user.setId(1L);
        user.setUsername("demo");
        user.setNickname("demo");
        user.setPassword("123456");
        when(mapper.selectOne(any())).thenReturn(user);

        AuthServiceImpl service = new AuthServiceImpl(mapper, jwtUtil);
        LoginRequest request = new LoginRequest();
        request.setUsername("demo");
        request.setPassword("123456");

        LoginResponse response = service.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("demo", response.getUser().getNickname());
        assertNotEquals("123456", user.getPassword());
        assertTrue(user.getPassword().startsWith("$2"));
        verify(mapper).updateById(user);

        LoginResponse secondResponse = service.login(request);
        assertNotNull(secondResponse);
        assertNotNull(secondResponse.getToken());
    }

    @Test
    void loginReturnsNullWhenPasswordMismatch() {
        UserInfoMapper mapper = mock(UserInfoMapper.class);
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);
        UserInfo user = new UserInfo();
        user.setId(1L);
        user.setUsername("demo");
        user.setNickname("demo");
        user.setPassword("123456");
        when(mapper.selectOne(any())).thenReturn(user);

        AuthServiceImpl service = new AuthServiceImpl(mapper, jwtUtil);
        LoginRequest request = new LoginRequest();
        request.setUsername("demo");
        request.setPassword("wrong-password");

        assertNull(service.login(request));
    }

    @Test
    void registerCreatesUserAndReturnsLoginResponseWithHashedPassword() {
        UserInfoMapper mapper = mock(UserInfoMapper.class);
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);
        when(mapper.selectOne(any())).thenReturn(null);
        doAnswer(invocation -> {
            UserInfo arg = invocation.getArgument(0);
            arg.setId(2L);
            return 1;
        }).when(mapper).insert(any(UserInfo.class));

        AuthServiceImpl service = new AuthServiceImpl(mapper, jwtUtil);
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setNickname("Alice");
        request.setPassword("abc123");
        request.setConfirmPassword("abc123");

        LoginResponse response = service.register(request);

        assertNotNull(response);
        assertEquals("alice", response.getUser().getUsername());
        assertEquals("Alice", response.getUser().getNickname());
        assertNotNull(response.getToken());

        ArgumentCaptor<UserInfo> captor = ArgumentCaptor.forClass(UserInfo.class);
        verify(mapper).insert(captor.capture());
        assertEquals("alice", captor.getValue().getUsername());
        assertEquals("Alice", captor.getValue().getNickname());
        assertNotEquals("abc123", captor.getValue().getPassword());
        assertTrue(captor.getValue().getPassword().startsWith("$2"));
    }

    @Test
    void registerThrowsWhenUsernameAlreadyExists() {
        UserInfoMapper mapper = mock(UserInfoMapper.class);
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);
        UserInfo existing = new UserInfo();
        existing.setId(9L);
        existing.setUsername("alice");
        when(mapper.selectOne(any())).thenReturn(existing);

        AuthServiceImpl service = new AuthServiceImpl(mapper, jwtUtil);
        RegisterRequest request = new RegisterRequest();
        request.setUsername("alice");
        request.setNickname("Alice");
        request.setPassword("abc123");
        request.setConfirmPassword("abc123");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> service.register(request));
        assertEquals("用户名已存在", exception.getMessage());
    }

    @Test
    void changePasswordUpdatesStoredPasswordToHashWhenOldPasswordMatches() {
        UserInfoMapper mapper = mock(UserInfoMapper.class);
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);
        UserInfo user = new UserInfo();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("123456");
        when(mapper.selectById(eq(1L))).thenReturn(user);

        AuthServiceImpl service = new AuthServiceImpl(mapper, jwtUtil);
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("123456");
        request.setNewPassword("newpass");
        request.setConfirmPassword("newpass");

        com.lifeapp.auth.AuthContext.setUserId(1L);
        try {
            service.changePassword(request);
        } finally {
            com.lifeapp.auth.AuthContext.clear();
        }

        assertNotEquals("newpass", user.getPassword());
        assertTrue(user.getPassword().startsWith("$2"));
        verify(mapper).updateById(user);
    }

    @Test
    void changePasswordThrowsWhenOldPasswordDoesNotMatch() {
        UserInfoMapper mapper = mock(UserInfoMapper.class);
        JwtUtil jwtUtil = new JwtUtil(TEST_SECRET);
        UserInfo user = new UserInfo();
        user.setId(1L);
        user.setUsername("demo");
        user.setPassword("123456");
        when(mapper.selectById(eq(1L))).thenReturn(user);

        AuthServiceImpl service = new AuthServiceImpl(mapper, jwtUtil);
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("wrong");
        request.setNewPassword("newpass");
        request.setConfirmPassword("newpass");

        com.lifeapp.auth.AuthContext.setUserId(1L);
        try {
            BadRequestException exception = assertThrows(BadRequestException.class, () -> service.changePassword(request));
            assertEquals("旧密码错误", exception.getMessage());
        } finally {
            com.lifeapp.auth.AuthContext.clear();
        }

        assertFalse("newpass".equals(user.getPassword()));
    }
}
