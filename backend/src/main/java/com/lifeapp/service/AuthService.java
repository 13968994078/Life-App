package com.lifeapp.service;

import com.lifeapp.dto.ChangePasswordRequest;
import com.lifeapp.dto.LoginRequest;
import com.lifeapp.dto.RegisterRequest;
import com.lifeapp.dto.UpdateProfileRequest;
import com.lifeapp.vo.LoginResponse;
import com.lifeapp.vo.UserProfile;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);

    void changePassword(ChangePasswordRequest request);

    UserProfile currentUser();

    UserProfile updateProfile(UpdateProfileRequest request);
}
