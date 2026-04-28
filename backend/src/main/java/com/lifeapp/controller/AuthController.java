package com.lifeapp.controller;

import com.lifeapp.dto.ChangePasswordRequest;
import com.lifeapp.common.ApiResponse;
import com.lifeapp.dto.LoginRequest;
import com.lifeapp.dto.RegisterRequest;
import com.lifeapp.service.AuthService;
import com.lifeapp.vo.LoginResponse;
import com.lifeapp.vo.UserProfile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response == null) {
            return ApiResponse.fail("账号或密码错误");
        }
        return ApiResponse.ok(response);
    }

    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok(authService.register(request));
    }

    @PutMapping("/password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.ok(null);
    }

    @GetMapping("/me")
    public ApiResponse<UserProfile> currentUser() {
        UserProfile profile = authService.currentUser();
        if (profile == null) {
            return ApiResponse.fail("用户不存在");
        }
        return ApiResponse.ok(profile);
    }
}
