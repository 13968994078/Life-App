package com.lifeapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lifeapp.auth.AuthContext;
import com.lifeapp.auth.JwtUtil;
import com.lifeapp.auth.PasswordHasher;
import com.lifeapp.common.BadRequestException;
import com.lifeapp.common.UnauthorizedException;
import com.lifeapp.dto.ChangePasswordRequest;
import com.lifeapp.dto.LoginRequest;
import com.lifeapp.dto.RegisterRequest;
import com.lifeapp.dto.UpdateProfileRequest;
import com.lifeapp.mapper.UserInfoMapper;
import com.lifeapp.model.UserInfo;
import com.lifeapp.service.AuthService;
import com.lifeapp.vo.LoginResponse;
import com.lifeapp.vo.UserProfile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserInfoMapper userInfoMapper;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserInfoMapper userInfoMapper, JwtUtil jwtUtil) {
        this.userInfoMapper = userInfoMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String username = normalizeUsername(request.getUsername());
        LambdaQueryWrapper<UserInfo> query = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUsername, username)
                .last("limit 1");
        UserInfo user = userInfoMapper.selectOne(query);
        if (user == null || !PasswordHasher.matches(request.getPassword(), user.getPassword())) {
            return null;
        }
        upgradeLegacyPasswordIfNeeded(user, request.getPassword());

        return toLoginResponse(user);
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        validatePasswordConfirmation(request.getPassword(), request.getConfirmPassword());

        String username = normalizeUsername(request.getUsername());
        LambdaQueryWrapper<UserInfo> query = new LambdaQueryWrapper<UserInfo>()
                .eq(UserInfo::getUsername, username)
                .last("limit 1");
        if (userInfoMapper.selectOne(query) != null) {
            throw new BadRequestException("用户名已存在");
        }

        LocalDateTime now = LocalDateTime.now();
        UserInfo user = new UserInfo();
        user.setUsername(username);
        user.setNickname(request.getNickname().trim());
        user.setPassword(PasswordHasher.hash(request.getPassword()));
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        userInfoMapper.insert(user);
        return toLoginResponse(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        validatePasswordConfirmation(request.getNewPassword(), request.getConfirmPassword());

        UserInfo user = currentUserEntity();
        if (!PasswordHasher.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("旧密码错误");
        }

        user.setPassword(PasswordHasher.hash(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userInfoMapper.updateById(user);
    }

    @Override
    public UserProfile currentUser() {
        return toProfile(currentUserEntity());
    }

    @Override
    public UserProfile updateProfile(UpdateProfileRequest request) {
        UserInfo user = currentUserEntity();
        user.setNickname(request.getNickname().trim());
        user.setAvatar(normalizeOptionalText(request.getAvatar()));
        user.setUpdatedAt(LocalDateTime.now());
        userInfoMapper.updateById(user);
        return toProfile(user);
    }

    private LoginResponse toLoginResponse(UserInfo user) {
        LoginResponse response = new LoginResponse();
        response.setToken(jwtUtil.generateToken(user.getId(), user.getNickname()));
        response.setUser(toProfile(user));
        return response;
    }

    private UserProfile toProfile(UserInfo user) {
        UserProfile profile = new UserProfile();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        return profile;
    }

    private UserInfo currentUserEntity() {
        Long userId = AuthContext.getUserId();
        if (userId == null) {
            throw new UnauthorizedException("请先登录");
        }

        UserInfo user = userInfoMapper.selectById(userId);
        if (user == null) {
            throw new UnauthorizedException("用户不存在");
        }
        return user;
    }

    private void upgradeLegacyPasswordIfNeeded(UserInfo user, String rawPassword) {
        if (!PasswordHasher.needsUpgrade(user.getPassword())) {
            return;
        }

        user.setPassword(PasswordHasher.hash(rawPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userInfoMapper.updateById(user);
    }

    private void validatePasswordConfirmation(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("两次输入的密码不一致");
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? null : username.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
